package org.ktb.modie.presentation.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.presentation.v1.dto.UpdateMeetRequest;
import org.ktb.modie.presentation.v1.dto.UpdatePaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
    ResponseEntity<SuccessResponse<CreateMeetResponse>> createMeet(
        @RequestAttribute("userId") String userId,
        @Valid @RequestBody CreateMeetRequest request
    );

    @Operation(summary = "모임 조회", description = "특정 모임 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음")
    })
    @GetMapping("/{meetId}")
    ResponseEntity<SuccessResponse<MeetDto>> getMeet(
        @Parameter(description = "조회할 모임 ID", example = "1")
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId
    );

    @Operation(summary = "모임 수정", description = "기존 모임 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
        @ApiResponse(responseCode = "403", description = "인증되지 않은 사용자")
    })
    @PatchMapping("/{meetId}")
    ResponseEntity<SuccessResponse<Void>> updateMeet(
        @PathVariable("meetId") String meetHashId,
        @RequestAttribute("userId") String userId,
        @Valid @RequestBody UpdateMeetRequest request
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
        @PathVariable("meetId") String meetHashId,
        @RequestAttribute("userId") String userId
    );

    @Operation(summary = "모임 목록 조회", description = "카테고리, 완료 여부, 페이지 번호로 필터링하여 모임 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "페이지 번호 또는 크기가 유효하지 않습니다.")
    })
    @GetMapping
    ResponseEntity<SuccessResponse<MeetListResponse>> getMeetList(
        @RequestAttribute("userId") String userId,
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
    ResponseEntity<SuccessResponse<Void>> createUserMeet(
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId
    );

    @Operation(summary = "모임 나가기", description = "사용자가 특정 모임에서 나갑니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모임 나가기 성공"),
        @ApiResponse(responseCode = "403", description = "모임 참여자만 탈퇴할 수 있음"),
        @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "종료된 모임은 나갈 수 없음")
    })
    @PatchMapping("/{meetId}/exit")
    ResponseEntity<SuccessResponse<Void>> deleteUserMeet(
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId
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
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId
    );

    @Operation(summary = "정산여부 업데이트", description = "정산 한 사람 isPayed : 0 -> 1 or 1 -> 0")
    @PatchMapping("/{meetId}/payments")
    ResponseEntity<SuccessResponse<Void>> updatePayments(
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId,
        @Valid @RequestBody UpdatePaymentRequest request
    );

    @Operation(summary = "정산금액 업데이트", description = "정산 금액 입력 0이상 10,000,000이하")
    @PatchMapping("/{meetId}/totalCost")
    ResponseEntity<SuccessResponse<Void>> updateTotalCost(
        @RequestAttribute("userId") String userId,
        @PathVariable("meetId") String meetHashId,
        @RequestBody @Min(0) @Max(10000000) int totalCost
    );
}
