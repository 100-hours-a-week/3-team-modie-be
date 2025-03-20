package org.ktb.modie.presentation.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdatePaymentResponse(
    @Schema(description = "정산 상태 변경 대상 유저 ID", example = "123456")
    String userId,

    @Schema(description = "정산 완료 여부 (1 = 완료, 0 = 미완료)", example = "1")
    boolean isPayed
) {
}
