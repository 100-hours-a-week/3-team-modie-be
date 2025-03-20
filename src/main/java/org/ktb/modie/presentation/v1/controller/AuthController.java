package org.ktb.modie.presentation.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.domain.User;
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

    @Autowired
    private UserService userService;

    // 카카오 로그인 페이지로 리디렉션하는 URL 생성
    @GetMapping("/auth/kakao/login")
    public ResponseEntity<SuccessResponse<Void>> kakaoLogin(@RequestParam("code") String code) {
        User user = userService.getKakaoUserInfo(code);
        System.out.println("code : " + code);
        System.out.println(user.getUserId());


        // 사용자 정보 반환 (예: JWT 토큰 생성 후 전달)
//        String token = jwtService.createToken(user); // JWT 토큰 생성 (사용자 정보 기반)

        // JWT 토큰을 프론트엔드에 반환
//        return ResponseEntity.ok(Map.of("token", token));
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
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
        User user = (User) model.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        return "main"; // main.html로 이동
    }
}
