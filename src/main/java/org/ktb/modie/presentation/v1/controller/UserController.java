package org.ktb.modie.presentation.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.UserResponse;
import org.ktb.modie.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {
    private final UserService userService;

    public ResponseEntity<SuccessResponse<UserResponse>> getUserProfile(String userId) {
        // TODO: 토큰을 userService로 넘겨주기
        UserResponse response = userService.getUserProfile(userId);

        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Map<String, Object>>> updateUserAccounts(
        String bankName,
        String accountNumber) {
        Map<String, Object> mockData = Map.of();

        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }
}
