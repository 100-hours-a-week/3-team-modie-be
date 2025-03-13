package org.ktb.modie.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API", description = "유저 테스트")
@Validated
@ApiResponses(
    value = {
        @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공.")
    })
public interface UserApi {
    @Operation(summary = "사용자 프로필 조회", description = "사용자 프로필 조회 API 호출")
    public ResponseEntity<SuccessResponse<Void>> getUserProfile(

    );

}
