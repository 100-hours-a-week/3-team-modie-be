package org.ktb.modie.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<SuccessResponse<MeetDto>> createMeet(
        @Valid @RequestBody MeetDto request
    );

    @Operation(summary = "모임 조회", description = "특정 모임 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음")
    })

    @GetMapping
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getMeet(
        @PathVariable("meetId") int meetId
    );

    @Operation(summary = "모임 수정", description = "기존 모임 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PatchMapping
    ResponseEntity<SuccessResponse<MeetDto>> updateMeet(
        @PathVariable("meetId") Long meetId,
        //@RequestHeader("Authorization") String authorization,
        @Valid @RequestBody MeetDto request
    );
}
