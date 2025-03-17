package org.ktb.modie.v1.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.ktb.modie.v1.dto.MeetListResponseDto;
import org.ktb.modie.v1.dto.MeetSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetController implements MeetApi {

    public ResponseEntity<SuccessResponse<MeetDto>> createMeet(
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetDto>> getMeet(int meetId
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

    public ResponseEntity<SuccessResponse<MeetDto>> updateMeet(int meetId,
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> deleteMeet(int meetId) {
        Map<String, Object> mockData = Map.of();
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<MeetListResponseDto>> getMeetList(String category, Integer completed,
        Integer page) {
        MeetListResponseDto meetListResponseDto = new MeetListResponseDto(
            1,
            10,
            47,
            List.of(
                new MeetSummaryDto(
                    1,
                    "제주 올레길 탐방",
                    "여행",
                    LocalDateTime.of(2025, 3, 20, 10, 0),
                    "제주특별자치도 제주시 월성로 4길 19",
                    "노블레스호텔 정문",
                    true,
                    2,
                    3,
                    "김박박즐"
                ),
                new MeetSummaryDto(
                    2,
                    "내일 점심 파스타먹을사람 3명띰",
                    "맛집",
                    LocalDateTime.of(2025, 3, 16, 13, 30),
                    "제주도 제주시 월성로4길 19",
                    "노블레스 호텔 후문",
                    false,
                    4,
                    10,
                    "void.yeon(연시완)"
                )
            )
        );
        return SuccessResponse.of(meetListResponseDto).asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> joinMeet(int meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Void>> exitMeet(int meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }
    
    public ResponseEntity<SuccessResponse<Void>> completeMeet(int meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<Map<String, Object>>> updatePayments(
        @PathVariable int meetId,
        @RequestParam int userId,
        @RequestBody int isPayed) {

        Map<String, Object> mockData = new LinkedHashMap<>();
        mockData.put("userId", userId);
        mockData.put("isPayed", isPayed); // service logic에 0->1 , 1->0 구현예정

        return SuccessResponse.of(mockData).asHttp(HttpStatus.OK);
    }
}
