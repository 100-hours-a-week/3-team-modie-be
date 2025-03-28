package org.ktb.modie.presentation.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.presentation.v1.dto.UpdateMeetRequest;
import org.ktb.modie.presentation.v1.dto.UpdatePaymentRequest;
import org.ktb.modie.service.MeetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MeetController implements MeetApi {

    @Autowired
    private final MeetService meetService;

    public ResponseEntity<SuccessResponse<CreateMeetResponse>> createMeet(String userId,
        CreateMeetRequest request
    ) {
        CreateMeetResponse response = meetService.createMeet(userId, request);

        //String userId = userService.getKakao
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(String userId, Long meetId
    ) {
        MeetDto response = meetService.getMeet(userId, meetId);

        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateMeet(
        Long meetId,
        String userId,
        @RequestBody UpdateMeetRequest request
    ) {
        meetService.updateMeet(userId, meetId, request);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> deleteMeet(Long meetId, String userId) {
        meetService.deleteMeet(meetId, userId);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetListResponse>> getMeetList(String userId, String category,
        boolean completed, int page
    ) {
        MeetListResponse response = meetService.getMeetList(userId, category, completed, page);
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> createUserMeet(String userId, Long meetId) {
        meetService.createUserMeet(userId, meetId);
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

    public ResponseEntity<SuccessResponse<Void>> updatePayments(String userId, Long meetId,
        UpdatePaymentRequest request) {
        meetService.updatePaymentStatus(userId, meetId, request);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateTotalCost(String userId, Long meetId,
        int totalCost) {
        meetService.updateTotalCost(userId, meetId, totalCost);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

}
