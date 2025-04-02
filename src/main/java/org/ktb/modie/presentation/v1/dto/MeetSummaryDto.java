package org.ktb.modie.presentation.v1.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record MeetSummaryDto(
    @Schema(description = "모임 ID 해시값", example = "A1b2C3d4")
    String meetHashId,

    @Schema(description = "모임 소개", example = "제주 올레길 탐방")
    String meetIntro,

    @Schema(description = "모임 유형", example = "여행")
    String meetType,

    @Schema(description = "모임 시작 시간", example = "2025-03-20T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime meetAt,

    @Schema(description = "주소", example = "제주특별자치도 제주시 월성로 4길 19")
    String address,

    @Schema(description = "주소 상세 설명", example = "노블레스호텔 정문")
    String addressDescription,

    @Schema(description = "비용 여부 (totalCost가 0보다 크면 true)", example = "true")
    boolean cost,

    @Schema(description = "참여 인원 수", example = "2")
    int memberCount,

    @Schema(description = "최대 인원 수", example = "3")
    int memberLimit,

    @Schema(description = "모임장 이름", example = "김박박즐")
    String ownerName
) {
}
