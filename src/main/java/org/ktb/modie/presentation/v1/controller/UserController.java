package org.ktb.modie.presentation.v1.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
import org.ktb.modie.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getUserProfile() {
        Map<String, Object> mockUserProfile = new LinkedHashMap<>();
        mockUserProfile.put("id", 1);
        mockUserProfile.put("name", "urung lee");
        mockUserProfile.put("profile", null);
        mockUserProfile.put("bank", "국민은행");
        mockUserProfile.put("account", "44020201223408");

        return SuccessResponse.of(mockUserProfile).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateAccount(
        String userId,
        UpdateAccountRequest request) {

        userService.updateAccount(userId, request);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }
}
