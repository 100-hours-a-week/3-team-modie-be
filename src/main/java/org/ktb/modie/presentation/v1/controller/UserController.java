package org.ktb.modie.presentation.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.FcmTokenRequest;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
import org.ktb.modie.presentation.v1.dto.UserResponse;
import org.ktb.modie.service.FcmService;
import org.ktb.modie.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final FcmService fcmService;

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

    public ResponseEntity<SuccessResponse<Void>> saveFcmToken(
        String userId,
        FcmTokenRequest reqeust) {

        fcmService.saveFcmToken(userId, reqeust);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.CREATED);
    }

}
