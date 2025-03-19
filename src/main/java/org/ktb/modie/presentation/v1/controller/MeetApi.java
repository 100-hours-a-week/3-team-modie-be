package org.ktb.modie.presentation.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;

@Tag(name = "Meet API", description = "모임 관련 API")
@Validated
@RequestMapping("/api/v1/meets")
public interface MeetApi {

    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임이 성공적으로 생성됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<CreateMeetResponse>> createMeet(
        @RequestParam("userId") String userId,
        @Valid @RequestBody CreateMeetRequest request
    );

    @Operation(summary = "모임 조회", description = "특정 모임 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음")
    })
    @GetMapping("/{meetId}")
    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(
        @Parameter(description = "조회할 모임 ID", example = "1")
        @PathVariable("meetId") Long meetId
    );

    @Operation(summary = "모임 수정", description = "기존 모임 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PatchMapping("/{meetId}")
    ResponseEntity<SuccessResponse<MeetDto>> updateMeet(
        @PathVariable("meetId") Long meetId,
        @Valid @RequestBody MeetDto request
    );

    @Operation(summary = "모임 삭제", description = "기존 모임을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 모임")
    })
    @DeleteMapping("/{meetId}")
    ResponseEntity<SuccessResponse<Void>> deleteMeet(
        @PathVariable("meetId") Long meetId
    );

    @Operation(summary = "모임 목록 조회", description = "카테고리, 완료 여부, 페이지 번호로 필터링하여 모임 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "페이지 번호 또는 크기가 유효하지 않습니다.")
    })
    @GetMapping
    ResponseEntity<SuccessResponse<MeetListResponse>> getMeetList(
        @RequestParam(value = "category", required = false) String category,
        @RequestParam(value = "completed", required = false, defaultValue = "0") boolean completed,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page
    );

    @Operation(summary = "모임 참여", description = "해당 모임에 참여합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임이 성공적으로 생성됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PostMapping("/{meetId}")
    public ResponseEntity<SuccessResponse<Void>> joinMeet(
        @RequestParam("userId") String userId,
        @PathVariable("meetId") Long meetId
    );

    @Operation(summary = "모임 나가기", description = "사용자가 특정 모임에서 나갑니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 나가기 성공"),
        @ApiResponse(responseCode = "403", description = "모임 참여자만 탈퇴할 수 있음"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "종료된 모임은 나갈 수 없음")
    })
    @PatchMapping("/{meetId}/exit")
    ResponseEntity<SuccessResponse<Void>> exitMeet(
        @PathVariable("meetId") Long meetId
    );

    @Operation(summary = "모임 종료", description = "모임을 종료합니다. 정산이 완료된 상태에서만 가능합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 종료 성공"),
        @ApiResponse(responseCode = "403", description = "모임 생성자만 모임을 종료할 수 있음"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "정산 완료 후 종료 가능")
    })

    @PatchMapping("/{meetId}/complete")
    ResponseEntity<SuccessResponse<Void>> completeMeet(
        @RequestParam("userId") String userId,
        @PathVariable("meetId") Long meetId
    );

    @Operation(summary = "정산내역 업데이트", description = "정산 한 사람 isPayed : 0 -> 1 or 1 -> 0")
    @PatchMapping("/{meetId}/payments")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> updatePayments(
        @Parameter(description = "정산 관리할 모임의 Id 값")
        @PathVariable(value = "meetId")
        @Digits(integer = 10000, fraction = 0, message = "모임ID")
        Long meetId,

        @Parameter(description = "정산한 유저 Id")
        @RequestParam(value = "userId", defaultValue = "12345")
        Long userId,

        @Parameter(description = "정산 여부")
        @RequestParam(value = "isPayed", defaultValue = "1")
        boolean isPayed
    );
}
