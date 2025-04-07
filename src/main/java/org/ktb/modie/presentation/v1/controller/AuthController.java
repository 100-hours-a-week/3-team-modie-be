package org.ktb.modie.presentation.v1.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.LoginResponse;
import org.ktb.modie.service.JwtService;
import org.ktb.modie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("user") // 세션에 사용자 정보를 저장
public class AuthController {

    // 인가 코드 재사용 방지를 위한 메모리 저장소
    private final Map<String, Boolean> usedAuthorizationCodes = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/auth/kakao/login")
    public ResponseEntity<SuccessResponse<?>> kakaoLogin(
        @RequestParam("code") String code,
        @RequestParam(value = "deviceId", required = false) String deviceId) {
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

            // deviceId가 없으면 UUID 생성
            if (deviceId == null || deviceId.isEmpty()) {
                deviceId = UUID.randomUUID().toString();
            }

            // FcmToken 저장/갱신
            userService.saveOrUpdateFcmToken(user, deviceId);

            // JWT 토큰 생성 (사용자 정보 기반)
            String token = jwtService.createToken(user);

            return ResponseEntity.ok(SuccessResponse.of(new LoginResponse(token, deviceId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SuccessResponse.of("Error during login: " + e.getMessage()));
        }
    }

    // 로그아웃 처리
    @GetMapping("/auth/kakao/logout")
    public String kakaoLogout(Model model) {
        model.addAttribute("user", null); // 세션에서 사용자 정보 삭제
        return "redirect:/";
    }

    // 메인 화면으로 리디렉션
    @GetMapping("/main")
    public String main(Model model) {
        User user = (User)model.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        return "main"; // main.html로 이동
    }
}
