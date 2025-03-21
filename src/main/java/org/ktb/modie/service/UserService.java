package org.ktb.modie.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.ktb.modie.domain.User;
import org.ktb.modie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate; // RestTemplate을 빈으로 관리하도록 수정

    // 카카오 로그인 후 Access Token을 가져오는 메서드
    public User getKakaoUserInfo(String code) {
        // Step 1: Access Token을 얻기 위한 요청
        String tokenUrl = KAKAO_TOKEN_URL + "?grant_type=authorization_code"
            + "&client_id=" + kakaoClientId
            + "&redirect_uri=" + kakaoRedirectUri
            + "&code=" + code;

        try {
            System.out.println("Access Token 요청 URL: " + tokenUrl); // 토큰 요청 URL 로그
            // Access Token 요청
            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST,
                new HttpEntity<>(new HttpHeaders()), Map.class);

            System.out.println("Access Token 응답: " + response.getBody()); // 응답 내용 로그

            String accessToken = (String)response.getBody().get("access_token");
            String refreshToken = (String)response.getBody().get("refresh_token");

            // Step 2: Access Token이 없으면 Refresh Token으로 새 Access Token 발급
            if (accessToken == null && refreshToken != null) {
                System.out.println("Access Token이 없어서 Refresh Token으로 새로 발급 시도");
                accessToken = refreshAccessToken(refreshToken);
            }

            System.out.println("Access Token: " + accessToken); // Access Token 확인

            // Step 3: 카카오 API로부터 사용자 정보 조회
            String userInfoUrl = KAKAO_API_URL + "?access_token=" + accessToken;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("사용자 정보 조회 URL: " + userInfoUrl); // 사용자 정보 조회 URL 로그

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
                entity, Map.class);

            System.out.println("사용자 정보 응답: " + userInfoResponse.getBody()); // 사용자 정보 응답 로그

            // 카카오 사용자 정보 파싱
            Object kakaoIdObject = userInfoResponse.getBody().get("id");
            Long kakaoId = null;

            if (kakaoIdObject instanceof Long) {
                kakaoId = (Long)kakaoIdObject;
            } else if (kakaoIdObject instanceof Integer) {
                kakaoId = ((Integer)kakaoIdObject).longValue();
            }

            if (kakaoId == null) {
                System.out.println("카카오 ID가 없습니다!"); // 카카오 ID 없는 경우
            }

            String userId = String.valueOf(kakaoId);  // Long을 String으로 변환

            String userName = (String)((Map)userInfoResponse.getBody().get("properties")).get("nickname");
            String profileImageUrl = (String)((Map)userInfoResponse.getBody().get("properties")).get("profile_image");

            System.out.println("사용자 이름: " + userName); // 사용자 이름 로그
            System.out.println("프로필 이미지 URL: " + profileImageUrl); // 프로필 이미지 URL 로그

            // Step 4: DB에서 사용자 조회, 없다면 새로 추가
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                System.out.println("새로운 사용자 등록: " + userName); // 새로운 사용자 등록 로그
                user = User.builder()
                    .userId(userId)
                    .userName(userName)
                    .profileImageUrl(profileImageUrl)
                    .createdAt(LocalDateTime.now())
                    .build();
                userRepository.save(user);
            }

            return user;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            // 400 Bad Request일 경우(즉, code가 만료되었을 경우)
            if (e.getStatusCode().value() == 400) {
                System.out.println("인증 코드 만료: " + e.getMessage()); // 인증 코드 만료 로그
                throw new RuntimeException("인증 코드가 만료되었습니다. 다시 로그인해주세요.");
            }
            // 카카오 API 호출 오류 처리
            System.out.println("카카오 로그인 API 호출 오류: " + e.getMessage()); // API 호출 오류 로그
            throw new RuntimeException("카카오 로그인 API 호출 오류: " + e.getMessage());
        }
    }

    // Step 5: Refresh Token을 이용해 새 Access Token을 발급 받는 메서드
    private String refreshAccessToken(String refreshToken) {
        String refreshTokenUrl = KAKAO_TOKEN_URL + "?grant_type=refresh_token"
            + "&client_id=" + kakaoClientId
            + "&refresh_token=" + refreshToken;

        try {
            // Refresh Token을 이용해 새로운 Access Token 발급
            ResponseEntity<Map> response = restTemplate.exchange(refreshTokenUrl, HttpMethod.POST,
                new HttpEntity<>(new HttpHeaders()), Map.class);
            return (String)response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            // 토큰 갱신 실패 시 예외 처리
            throw new RuntimeException("Refresh Token으로 Access Token 갱신 실패: " + e.getMessage());
        }
    }
}
