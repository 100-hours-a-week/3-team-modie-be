package org.ktb.modie.presentation.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.UserResponse;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User API", description = "유저 테스트")
@Validated
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "사용자 프로필 조회", description = "사용자 프로필 조회 API 호출")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공."),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자 입니다."),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<UserResponse>> getUserProfile(
        @Parameter(description = "유저아이디")
        @RequestParam(value = "userId", defaultValue = "12345")
        @PathVariable
        String userId
    );

    @Operation(summary = "계좌번호 업데이트", description = "사용자 계좌번호 업데이트")
    @PatchMapping("/accounts")
    public ResponseEntity<SuccessResponse<Void>> updateAccount(
        @RequestParam("userId") String userId,
        @Valid @RequestBody UpdateAccountRequest request
    );
}
