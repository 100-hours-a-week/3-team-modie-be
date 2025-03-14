package org.ktb.modie.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class MeetController implements MeetApi {

    @PostMapping("/api/v1/meets")
    public ResponseEntity<SuccessResponse<MeetDto>> createMeet(
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    @GetMapping("/api/v1/meets/{meetId}")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getMeet(
        @Parameter(description = "조회할 모임 ID", example = "1")
        @PathVariable("meetId") int meetId
    ) {
        Map<String, Object> mockData = Map.of(
            "meetId", meetId,
            "meetIntro", "제주 해안 드라이브",
            "meetType", "여행",
            "address", "제주특별자치도 제주시 월성로 4길 19",
            "meetAt", "2025-02-20T18:00:00",
            "memberLimit", 5,
            "totalCost", 10000
        );
        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }

}
