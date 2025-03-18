package org.ktb.modie.presentation.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "모임 생성 요청 DTO")
public record CreateMeetRequest(
    @Schema(description = "모임 설명", example = "제주 올레길 탐방")
    @NotBlank
    @Size(max = 30)
    String meetIntro,

    @Schema(description = "모임 유형 ('이동', '운동', '빨래' 등)", example = "여행")
    @NotBlank
    @Size(max = 10)
    String meetType,

    @Schema(description = "주소", example = "제주특별자치도 제주시 월성로 4길 19")
    @NotBlank
    @Size(max = 40)
    String address,

    @Schema(description = "주소 설명", example = "노블레스 관광 호텔 로비")
    @NotBlank
    @Size(max = 20)
    String addressDescription,

    @Schema(description = "출발 시간", example = "2025-02-20T18:00:00")
    @NotNull
    LocalDateTime meetAt,

    @Schema(description = "총 비용 (0~10,000,000)", example = "10000")
    @NotNull
    @Min(0)
    @Max(10_000_000)
    Long totalCost,

    @Schema(description = "최대 인원 수 (1~30)", example = "5")
    @NotNull
    @Min(1)
    @Max(30)
    Long memberLimit
) {
}
