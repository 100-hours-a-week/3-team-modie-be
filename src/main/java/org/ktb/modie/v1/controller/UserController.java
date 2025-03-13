package org.ktb.modie.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
    @GetMapping("/api/v1/users")
    public ResponseEntity<SuccessResponse<Void>> getUserProfile() {

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }
}
