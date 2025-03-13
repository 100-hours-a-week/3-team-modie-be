package org.ktb.modie.v1.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MeetDto {

    @Parameter(description = "모임 소개", example = "제주 해안 드라이브")
    @NotNull
    @Size(max = 30)
    private String meetIntro;

    @Parameter(description = "모임 유형 ('이동', '운동', '빨래' 등)", example = "여행")
    @NotNull
    @Size(max = 10)
    private String meetType;

    @Parameter(description = "주소", example = "제주특별자치도 제주시 월성로 4길 19")
    @NotNull
    @Size(max = 40)
    private String address;

    @Parameter(description = "주소 상세 설명", example = "애월 해안도로")
    @Size(max = 20)
    private String addressDescription;

    @Parameter(description = "모임 시작 시간 (ISO 형식)", example = "2025-02-20T18:00:00")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime meetAt;

    @Parameter(description = "총 발생 비용 (0~10,000,000)", example = "10000")
    @Min(0)
    @Max(10_000_000)
    private Integer totalCost;

    @Parameter(description = "최대 인원 수 (1~30)", example = "5")
    @Min(1)
    @Max(30)
    private Integer memberLimit;

}
