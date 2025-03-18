package org.ktb.modie.presentation.v1.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record MeetListResponse(
    @Schema(description = "페이지 번호", example = "1")
    int page,

    @Schema(description = "페이지 크기", example = "10")
    int size,

    @Schema(description = "전체 요소 수", example = "47")
    long totalElements,

    @Schema(description = "모임 리스트")
    List<MeetSummary> meets
) {
}
