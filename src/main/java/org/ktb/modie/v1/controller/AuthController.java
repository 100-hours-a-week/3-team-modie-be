package org.ktb.modie.v1.controller;

import org.ktb.modie.domain.User;
import org.ktb.modie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("user") // 세션에 사용자 정보를 저장
public class AuthController {

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
    public String kakaoCallback(@RequestParam("code") String code, Model model) {
        // 카카오 로그인 콜백에서 받은 code로 사용자 정보 조회
        User user = userService.getKakaoUserInfo(code);
        // 세션에 사용자 정보 저장
        model.addAttribute("user", user); // @SessionAttributes가 자동으로 세션에 저장

        // 홈 화면으로 리디렉션
        return "main";
    }

    // 로그아웃 처리
    @GetMapping("/auth/kakao/logout")
    public String kakaoLogout(Model model) {
        model.addAttribute("user", null); // 세션에서 사용자 정보 삭제
        return "redirect:/"; // 로그아웃 후 홈으로 리디렉션
    }

    // 메인 화면으로 리디렉션
    @GetMapping("/main")
    public String main(Model model) {
        User user = (User)model.getAttribute("user");
        if (user == null) {
            return "redirect:/"; // 세션에 사용자가 없으면 홈으로 리디렉션
        }
        return "main"; // main.html로 이동
    }

}
