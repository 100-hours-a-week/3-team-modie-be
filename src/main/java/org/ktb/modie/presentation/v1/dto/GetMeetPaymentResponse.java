package org.ktb.modie.presentation.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetMeetPaymentResponse(
    @Schema(description = "은행 이름", example = "신한은행")
    String bankName,

    @Schema(description = "계좌 번호", example = "11045678901234")
    String accountNumber,

    @Schema(description = "모임 인원", example = "3")
    int memberCount
) {
}
