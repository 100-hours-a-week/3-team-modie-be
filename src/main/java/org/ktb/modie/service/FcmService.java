package org.ktb.modie.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.FcmToken;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.FcmTokenRequest;
import org.ktb.modie.repository.FcmTokenRepository;
import org.ktb.modie.repository.UserRepository;
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
import jakarta.transaction.Transactional;

@Service
public class FcmService {

    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final String fcmApiUrl;
    private final String credentialsPath;

    private String accessToken;

    public FcmService(
        UserRepository userRepository,
        FcmTokenRepository fcmTokenRepository,
        @Value("${fcm.project-id}") String projectId,
        @Value("${fcm.credentials.path}") String credentialsPath
    ) {
        this.userRepository = userRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.fcmApiUrl = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
        this.credentialsPath = credentialsPath.replace("src/main/resources/", "");
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource serviceAccount = new ClassPathResource(credentialsPath);
            GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccount.getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            accessToken = googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new IllegalStateException("FCM 인증 토큰 초기화 실패", e);
        }
    }

    @Transactional
    public void saveFcmToken(String userId, FcmTokenRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        String token = request.getToken();
        String deviceType = request.getDeviceType();
        // 매번 업데이트하는 걸로(v1)
        fcmTokenRepository.findByUser_UserId(userId)
            .ifPresentOrElse(existingToken -> {
                existingToken.setToken(token);
                existingToken.setDeviceType(deviceType);
            }, () -> {
                FcmToken newToken = FcmToken.builder()
                    .user(user)
                    .token(token)
                    .deviceType(deviceType)
                    .build();
                fcmTokenRepository.save(newToken);
            });
    }

    public void sendNotification(String targetToken, String title, String body) {
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

            ResponseEntity<Map> response = restTemplate.postForEntity(fcmApiUrl, request, Map.class);
            System.out.println("FCM 응답: " + response.getBody().get("name"));
        } catch (Exception e) {
            System.out.println("FCM 전송 실패" + e);
        }
    }

}
