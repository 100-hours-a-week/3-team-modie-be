package org.ktb.modie.presentation.v1.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "모임 수정 DTO")
@Builder
public record UpdateMeetRequest(
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
    @FutureOrPresent(message = "출발 시간은 현재 시간보다 이후여야 합니다.")
    LocalDateTime meetAt,

    @Schema(description = "총 비용 (0~10,000,000)", example = "10000")
    @NotNull
    @Min(0)
    @Max(10_000_000)
    int totalCost,

    @Schema(description = "최대 인원 수 (1~30)", example = "5")
    @NotNull
    @Min(1)
    @Max(30)
    int memberLimit

    // @Schema(description = "수정시각", example = "2025-03-18T18:00:00")
    // @NotNull
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // LocalDateTime updatedAt
) {
}
