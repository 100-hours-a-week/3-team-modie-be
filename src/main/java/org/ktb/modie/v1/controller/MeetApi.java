package org.ktb.modie.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "모임 API", description = "모임 관련 API")
@Validated
public interface MeetApi {

    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임이 성공적으로 생성됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createMeet(
        @Valid @RequestBody MeetDto request
    );

}
