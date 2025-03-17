package org.ktb.modie.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/meets")
public class MeetController implements MeetApi {

    @PostMapping
    public ResponseEntity<SuccessResponse<MeetDto>> createMeet(
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    @GetMapping("/{meetId}")
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

    @PatchMapping("/{meetId}")
    public ResponseEntity<SuccessResponse<MeetDto>> updateMeet(int meetId,
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    @DeleteMapping("/{meetId}")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> deleteMeet(
        @Parameter(description = "삭제할 모임 ID", example = "1") int meetId) {
        Map<String, Object> mockData = Map.of();
        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }
}
