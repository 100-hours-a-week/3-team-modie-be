package org.ktb.modie.presentation.v1.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.service.MeetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MeetController implements MeetApi {

    private final MeetService meetService;

    public ResponseEntity<SuccessResponse<CreateMeetResponse>> createMeet(
        //@RequestHeader("Authorization") String authToken,
        @RequestParam String userId,
        @RequestBody CreateMeetRequest request
    ) {
        CreateMeetResponse response = meetService.createMeet(userId, request);

        //String userId = userService.getKakao
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(Long meetId
    ) {
        MeetDto meetDto = new MeetDto(
            meetId,
            "제주 해안 드라이브",
            "여행",
            "제주특별자치도 제주시 월성로 4길 19",
            "노블레스 관광호텔 로비",
            LocalDateTime.of(2025, 3, 20, 16, 13, 30),
            5,
            10000
        );
        return SuccessResponse.of(meetDto).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> updateMeet(Long meetId,
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> deleteMeet(Long meetId) {
        Map<String, Object> mockData = Map.of();
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetListResponse>> getMeetList(String category, boolean completed, int page
    ) {
        MeetListResponse response = meetService.getMeetList(category, completed, page);
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> joinMeet(String userId, Long meetId) {
        meetService.joinMeet(userId, meetId);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> exitMeet(Long meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> completeMeet(Long meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Map<String, Object>>> updatePayments(
        @PathVariable Long meetId,
        @RequestParam Long userId,
        @RequestBody boolean isPayed) {

        Map<String, Object> mockData = new LinkedHashMap<>();
        mockData.put("userId", userId);
        mockData.put("isPayed", isPayed); // service logic에 0->1 , 1->0 구현예정

        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }
}
