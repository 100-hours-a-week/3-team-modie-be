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
			String fixedKey = ("-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFA"
				+ "ASCBKcwggSjAgEAAoIBAQCzeUOUBEH+Y8VQ\noOypjiqOq6vb9UggwZp2XWWXor9yVJEloj+5jRSjiW9znH09BHZ"
				+ "1Q8uYUxD9RxML\ndX9ADSMJpFdge7KNMukyTZHxorFYn9Myt0gt4sdEQIC9wmelaR2+qU4k7XNaD1py\n+GGBidbo"
				+ "+TwN2rHnExX4lQvtTiU2d5vIJJrDz4nONgjnRY1pW3JmE1J2L372jUAc\ngcb0ZnbjF8l739WD+OHi0vth4fmCNDUt"
				+ "vqvvWgH6OTzIDMUYFvkgwfgrGYIR8toJ\nihP1qyOADyBAqtg/CBMRh6Sq2Wx+3tH72YZnDRHHhOit/4SrlvN19yIz"
				+ "hPFhI1OY\n8u2GE9t7AgMBAAECggEAF8u65g6FIXw2VoZqWot69lz/SwlZKDQDODER8z+k2pJ+\nhtrdpXXLMr5aL0"
				+ "ikYklhML3DFM3kBsY8WYdD+UjEndaoWdE3efkyFMqsJbmd11Ok\nO6k0F2wQt5VF0eMgvZG+W9/ezePzKLxjEF4BBP"
				+ "KIXW4ORFf8FoM1T+GTKezz33tV\ni+rH/+/c8qfQ7t7vJ42w9d34wfL6do3hSbuAvXKl2x3RW6J9znbvazv79jzfQY"
				+ "Yf\ni0OivF4um2sceDdDLkHN5RowkSA7KqnK3syYIyJUnI8f8HYMVixZ69LYHuBJ0bfn\nm0IJ04ERmgUG6cUTwxEH"
				+ "qWJ7SjIFO89XshENQdSIoQKBgQD44KYgugmzas0/RFdA\nITsgma0G2SSPjtYXcG+3LaQf19m8JYg+l9PSy5kePod0"
				+ "Y9Hwsto6NBop8SEIe4tJ\nPchTOQsS1+BFuvGW6gFwEF9D8paCLfkOGTFcvHtNDzRWcHBVsSSSDB4gq7Vh+dvS\nlp"
				+ "PmMouNul4C7HKmiIk/aGfn1QKBgQC4nCRJuE5k/GPFjO/aTxfv9dWK+jojzSGU\nAGy63DmezJjvtxkMs1xP9bJngI"
				+ "FxkHUDRUHqXbEzykV/5aJrd3v5kYcKY1hMjnti\nN4z+GEpFKxPsX78LHQ8AGTMJWNqHPzVKlewnI/imskLoqzkFFJ"
				+ "iEfcTtJtty0jQM\nmGyHQoEuDwKBgQCUPVs2ixlUIP13/cDqleCeHmsNdTFtwOqMs5AXT7w+q1cp1kE8\n5zpoiJ"
				+ "T/iscr8HkQIbgSiw46hUHjx5LDYWV9DLEn3CXd4ugbt+57TYUw98HmcHO0\njhT049+dmrzV78s5f3YE9+rKEvzLC"
				+ "wSAfuhBaUHufV+Z5jS0NQdrGUkqsQKBgCCu\nc7YhTK+QBtGoO6X4LpKqgJ1T4wsHe5RJ5vXFmuXD7qcxUNvwvLzj"
				+ "ECxGkaPbIPvt\nPNgSshYrDtavfmtsAaSS4Mc0AyO6bZlboDgzcKKCF8rWwGllQYyQ6XoUIsuiovqL\nQ/1B3vkTw"
				+ "ciS10tQ9BcwdLZPFLZlBBXevgfL/DZ9AoGAQJZQk1eXrvTVSZxDzn/7\nKygtJQH3JLJ0igbCTvBcz7mFO++zHwUy"
				+ "BcbfK6kAFwm346NBobpYrTuEMsi+VUy2\nv05afy8xs0zOtphb+Yg6joVajff7f/qrs2THaM5lD7/eIL/RpUvGi07"
				+ "D9N5ID8E3\ng0BWh4KUETh1Hk3VeJY0jXA=\n-----END PRIVATE KEY-----\n").replace(
				"\\n", "\n");
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

	public void sendNotification(String targetToken, String title, String body, String meetId) {
		try {
			Map<String, Object> message = new HashMap<>();
			Map<String, Object> notification = new HashMap<>();
			notification.put("title", title);
			notification.put("body", body);

			Map<String, Object> data = new HashMap<>();
			data.put("url", "https://modie.site/" + meetId + "/chat");

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
