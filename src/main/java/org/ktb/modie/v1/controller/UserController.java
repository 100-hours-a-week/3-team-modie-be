package org.ktb.modie.v1.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
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

    public ResponseEntity<SuccessResponse<Map<String, Object>>> updateUserAccounts(String bankName, String accountNumber) {
        Map<String,Object> mockData = Map.of();

        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }
}
