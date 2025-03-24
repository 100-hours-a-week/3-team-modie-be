package org.ktb.modie.presentation.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
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
        UserResponse response = userService.getUserProfile(userId);

        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateAccount(
        String userId,
        UpdateAccountRequest request) {

        userService.updateAccount(userId, request);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }
}
