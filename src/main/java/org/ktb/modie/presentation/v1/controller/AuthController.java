package org.ktb.modie.presentation.v1.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.domain.User;
import org.ktb.modie.service.JwtService;
import org.ktb.modie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // 인가 코드 재사용 방지를 위한 메모리 저장소
    private final Map<String, Boolean> usedAuthorizationCodes = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/auth/kakao/login")
    public ResponseEntity<SuccessResponse<String>> kakaoLogin(@RequestParam("code") String code) {
        try {
            // 인가 코드 재사용 방지 처리
            if (usedAuthorizationCodes.containsKey(code)) {
                return ResponseEntity.badRequest().body(SuccessResponse.of("Authorization code has already been used"));
            }

            // 인가 코드 사용 처리
            usedAuthorizationCodes.put(code, true);

            User user = userService.getKakaoUserInfo(code);
            System.out.println("User ID: " + user.getUserId());

            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                return ResponseEntity.badRequest().body(SuccessResponse.of("Invalid user ID"));
            }

            // JWT 토큰 생성 (사용자 정보 기반)
            String token = jwtService.createToken(user);
            System.out.println("Generated JWT Token: " + token);

            return ResponseEntity.ok(SuccessResponse.of(token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SuccessResponse.of("Error during login: " + e.getMessage()));
        }
    }

    // 로그아웃 처리
    @GetMapping("/auth/kakao/logout")
    public void kakaoLogout(Model model) {
    }
}
