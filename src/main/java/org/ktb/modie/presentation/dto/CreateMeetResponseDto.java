package org.ktb.modie.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "모임 생성 응답 DTO")
public record CreateMeetResponseDto(
    @Schema(description = "생성된 모임 ID", example = "1")
    Long meetId
) {
}
