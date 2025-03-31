package org.ktb.modie.service;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;

@Service
public class FcmService {

    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/modie-8ae56/messages:send";

    @Value("modie-firebase-adminsdk.json")
    private String resource;

    private String accessToken;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount = new ClassPathResource(resource).getInputStream();
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            accessToken = googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new IllegalStateException("FCM 인증 토큰 초기화 실패", e);
        }
    }

    public Map<String, Object> sendNotification(String targetToken, String title, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> message = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("token", targetToken);
            messageContent.put("notification", notification);

            message.put("message", messageContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(FCM_API_URL, request, Map.class);
            System.out.println("FCM 응답: " + response.getBody().get("name"));

            // Map 형태로 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("name", response.getBody().get("name"));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", data);

            return result;

        } catch (Exception e) {
            System.out.println("FCM 전송 실패" + e);
            throw new RuntimeException("FCM 전송 실패", e);
        }
    }
}
