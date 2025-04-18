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
        return SuccessResponse.of(response).asHttp(HttpStatus.CREATED);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(String userId, String meetHashId
    ) {
        MeetDto response = meetService.getMeet(userId, meetHashId);

        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateMeet(
        String meetHashId,
        String userId,
        @RequestBody UpdateMeetRequest request
    ) {
        meetService.updateMeet(userId, meetHashId, request);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> deleteMeet(String meetHashId, String userId) {
        meetService.deleteMeet(meetHashId, userId);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetListResponse>> getMeetList(String userId, String category,
        boolean completed, int page
    ) {
        MeetListResponse response = meetService.getMeetList(userId, category, completed, page);
        return SuccessResponse.of(response).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> createUserMeet(String userId, String meetHashId) {
        meetService.createUserMeet(userId, meetHashId);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> deleteUserMeet(String userId, String meetHashId) {
        meetService.deleteUserMeet(userId, meetHashId);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> completeMeet(String userId, String meetHashId) {
        meetService.completeMeet(userId, meetHashId);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updatePayments(String userId, String meetHashId,
        UpdatePaymentRequest request) {
        meetService.updatePaymentStatus(userId, meetHashId, request);
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> updateTotalCost(String userId, String meetHashId,
        int totalCost) {
        meetService.updateTotalCost(userId, meetHashId, totalCost);

        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

}
