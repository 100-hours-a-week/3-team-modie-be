package org.ktb.modie.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ktb.modie.config.FirebaseProperties;
import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.FcmToken;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.FcmTokenRequest;
import org.ktb.modie.repository.FcmTokenRepository;
import org.ktb.modie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmService {

	private final UserRepository userRepository;
	private final FcmTokenRepository fcmTokenRepository;
	private final FirebaseProperties firebaseProperties;
	private final RestTemplate restTemplate;

	private String fcmApiUrl;
	private GoogleCredentials googleCredentials;

	@Value("${fcm.project-id}")
	public void setFcmApiUrl(String projectId) {
		this.fcmApiUrl = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
	}

	@PostConstruct
	public void init() {
		try {
			this.googleCredentials = GoogleCredentials
				.fromStream(generateFirebaseCredentialStream())
				.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(CustomErrorCode.FCM_TOKEN_SET_FAILED);
		}
	}

	private InputStream generateFirebaseCredentialStream() {
		try {
			Map<String, Object> serviceAccount = new HashMap<>();
			serviceAccount.put("type", "service_account");
			serviceAccount.put("project_id", firebaseProperties.getProjectId());
			serviceAccount.put("private_key_id", firebaseProperties.getPrivateKeyId());
			ObjectMapper objectMapper = new ObjectMapper();
			String rawPrivateKey = firebaseProperties.getPrivateKey();
			String fixedKey = firebaseProperties.getPrivateKey().replace("\\n", "\n");
			System.out.println(rawPrivateKey);
			System.out.println(fixedKey);
			serviceAccount.put("private_key", fixedKey);
			serviceAccount.put("client_email", firebaseProperties.getClientEmail());
			serviceAccount.put("client_id", firebaseProperties.getClientId());
			serviceAccount.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
			serviceAccount.put("token_uri", "https://oauth2.googleapis.com/token");
			serviceAccount.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
			serviceAccount.put("client_x509_cert_url", firebaseProperties.getClientX509CertUrl());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			new ObjectMapper().writeValue(out, serviceAccount);
			return new ByteArrayInputStream(out.toByteArray());

		} catch (Exception e) {
			throw new BusinessException(CustomErrorCode.FCM_TOKEN_SET_FAILED);
		}
	}

	private String getAccessToken() {
		try {
			googleCredentials.refreshIfExpired();
			return googleCredentials.getAccessToken().getTokenValue();
		} catch (Exception e) {
			throw new BusinessException(CustomErrorCode.FCM_TOKEN_SET_FAILED);
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

	public void sendNotification(String targetToken, String title, String body, Long meetId) {
		try {
			Map<String, Object> message = new HashMap<>();
			Map<String, Object> notification = new HashMap<>();
			notification.put("title", title);
			notification.put("body", body);

			Map<String, Object> data = new HashMap<>();
			data.put("url", "/" + meetId + "/chat");

			Map<String, Object> messageContent = new HashMap<>();
			messageContent.put("token", targetToken);
			messageContent.put("notification", notification);
			messageContent.put("data", data);
			message.put("message", messageContent);

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(getAccessToken());
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
			restTemplate.postForEntity(fcmApiUrl, request, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(CustomErrorCode.FCM_SEND_FAILED);
		}
	}

}
