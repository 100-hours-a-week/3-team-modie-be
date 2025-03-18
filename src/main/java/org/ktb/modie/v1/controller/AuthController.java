package org.ktb.modie.v1.controller;

import org.ktb.modie.domain.User;
import org.ktb.modie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("user") // 세션에 사용자 정보를 저장
public class AuthController {
    @Autowired
    private HttpSession session;

    @Autowired
    private UserService userService;

    // 카카오 로그인 페이지로 리디렉션하는 URL
    @GetMapping("/auth/kakao/login")
    public String kakaoLogin() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + System.getenv("KAKAO_CLIENT_ID")
            + "&redirect_uri=" + System.getenv("KAKAO_REDIRECT_URI")
            + "&response_type=code";
        return "redirect:" + kakaoLoginUrl;
    }

    // 카카오 로그인 후 콜백 처리
    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        // 카카오 로그인 콜백에서 받은 code로 사용자 정보 조회
        User user = userService.getKakaoUserInfo(code);
        // 쿠키 설정 (유효기간: 7일)
        Cookie userIdCookie = new Cookie("userId", user.getUserId());
        userIdCookie.setMaxAge(7 * 24 * 60 * 60);
        userIdCookie.setPath("/");

        Cookie userNameCookie = new Cookie("userName", user.getUserName());
        userNameCookie.setMaxAge(7 * 24 * 60 * 60);
        userNameCookie.setPath("/");

        response.addCookie(userIdCookie);
        response.addCookie(userNameCookie);

        return "redirect:/main";
    }

    // 로그아웃 처리
    @GetMapping("/auth/kakao/logout")
    public String kakaoLogout(Model model) {
        model.addAttribute("user", null); // 세션에서 사용자 정보 삭제
        return "redirect:/"; // 로그아웃 후 홈으로 리디렉션
    }

    // 메인 화면 (쿠키에서 사용자 정보 가져오기)
    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        String userId = null;
        String userName = null;

        // 쿠키에서 userId와 userName 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                }
                if ("userName".equals(cookie.getName())) {
                    userName = cookie.getValue();
                }
            }
        }
        if (userId == null || userName == null) {
            return "redirect:/";
        }

        return "main"; // main.html로 이동
    }

}
