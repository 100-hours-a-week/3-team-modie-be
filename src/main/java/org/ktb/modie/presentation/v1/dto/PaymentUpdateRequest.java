package org.ktb.modie.presentation.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PaymentUpdateRequest(
    @Schema(description = "정산 상태를 변경할 유저 ID", example = "123")
    @NotNull(message = "userId는 필수 입력 값입니다.")
    String userId
) {
}
