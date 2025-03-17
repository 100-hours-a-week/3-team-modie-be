package org.ktb.modie.v1.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.ktb.modie.v1.dto.MeetListResponseDto;
import org.ktb.modie.v1.dto.MeetSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetController implements MeetApi {
    @Override
    public ResponseEntity<SuccessResponse<MeetDto>> createMeet(
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    @Override
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

    @Override
    public ResponseEntity<SuccessResponse<MeetDto>> updateMeet(int meetId,
        @RequestBody MeetDto request
    ) {
        return SuccessResponse.of(request).asHttp(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SuccessResponse<Void>> deleteMeet(int meetId) {
        Map<String, Object> mockData = Map.of();
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }

    @Override
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

    @Override
    public ResponseEntity<SuccessResponse<Void>> joinMeet(int meetId) {
        return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
    }
}
