package org.ktb.modie.presentation.v1.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.presentation.v1.dto.MeetSummaryDto;
import org.ktb.modie.presentation.v1.dto.UpdateMeetRequest;
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

    public ResponseEntity<SuccessResponse<CreateMeetResponse>> createMeet(String userId,
        CreateMeetRequest request
    ) {
        CreateMeetResponse response = meetService.createMeet(userId, request);

        //String userId = userService.getKakao
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(Long meetId
    ) {
        MeetDto response = meetService.getMeet(meetId);

        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateMeet(Long meetId, String userId,
        @RequestBody UpdateMeetRequest request
    ) {
        meetService.updateMeet(userId, meetId, request);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
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


    public ResponseEntity<SuccessResponse<Void>> deleteUserMeet(String userId, Long meetId) {
        meetService.deleteUserMeet(userId, meetId);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> completeMeet(String userId, Long meetId) {
        meetService.completeMeet(userId, meetId);
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
