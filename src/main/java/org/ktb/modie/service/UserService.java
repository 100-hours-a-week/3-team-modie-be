package org.ktb.modie.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.ktb.modie.domain.User;
import org.ktb.modie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private static final String KAKAO_CLIENT_ID = System.getenv("KAKAO_CLIENT_ID");
    private static final String KAKAO_REDIRECT_URI = System.getenv("KAKAO_REDIRECT_URI");
    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate; // RestTemplate을 빈으로 관리하도록 수정

    // 카카오 로그인 후 Access Token을 가져오는 메서드
    public User getKakaoUserInfo(String code) {
        System.out.println("code는 여기서 보자..." + code);
        // Step 1: Access Token을 얻기 위한 요청
        String tokenUrl = KAKAO_TOKEN_URL + "?grant_type=authorization_code"
            + "&client_id=" + KAKAO_CLIENT_ID
            + "&redirect_uri=" + KAKAO_REDIRECT_URI
            + "&code=" + code;

        try {
            // Access Token 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, null, Map.class);
            String accessToken = (String)response.getBody().get("access_token");
            String refreshToken = (String)response.getBody().get("refresh_token");

            // Step 2: Access Token이 없으면 Refresh Token으로 새 Access Token 발급
            if (accessToken == null) {
                accessToken = refreshAccessToken(refreshToken);
            }

            // Step 3: 카카오 API로부터 사용자 정보 조회
            String userInfoUrl = KAKAO_API_URL + "?access_token=" + accessToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
                new RequestEntity<>(HttpHeaders.EMPTY, HttpMethod.GET, null), Map.class);

            // 카카오 사용자 정보 파싱
            // "id" 값을 Object로 가져온 후 Long으로 캐스팅
            Object kakaoIdObject = userInfoResponse.getBody().get("id");
            Long kakaoId = null;

            if (kakaoIdObject instanceof Long) {
                kakaoId = (Long)kakaoIdObject;
            } else if (kakaoIdObject instanceof Integer) {
                // 경우에 따라 Integer일 수 있으므로 Integer -> Long 변환
                kakaoId = ((Integer)kakaoIdObject).longValue();
            }

            String userId = String.valueOf(kakaoId);  // Long을 String으로 변환

            String userName = (String)((Map)userInfoResponse.getBody().get("properties")).get("nickname");
            String profileImageUrl = (String)((Map)userInfoResponse.getBody().get("properties")).get("profile_image");

            // Step 4: DB에서 사용자 조회, 없다면 새로 추가
            User user = userRepository.findByUserId(userId);
            if (user == null) {
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
            // 400 Bad Request일 경우(즉, code가 만료되었을 경우)
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("인증 코드가 만료되었습니다. 다시 로그인해주세요.");
            }
            // 카카오 API 호출 오류 처리
            throw new RuntimeException("카카오 로그인 API 호출 오류: " + e.getMessage());
        }
    }

    // Step 5: Refresh Token을 이용해 새 Access Token을 발급 받는 메서드
    private String refreshAccessToken(String refreshToken) {
        String refreshTokenUrl = KAKAO_TOKEN_URL + "?grant_type=refresh_token"
            + "&client_id=" + KAKAO_CLIENT_ID
            + "&refresh_token=" + refreshToken;

        try {
            // Refresh Token을 이용해 새로운 Access Token 발급
            ResponseEntity<Map> response = restTemplate.postForEntity(refreshTokenUrl, null, Map.class);
            return (String)response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            // 토큰 갱신 실패 시 예외 처리
            throw new RuntimeException("Refresh Token으로 Access Token 갱신 실패: " + e.getMessage());
        }
    }
}
