package org.ktb.modie.presentation.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
    @Schema(description = "은행명", example = "신한은행")
    @Size(max = 10, message = "은행명은 최대 10자까지 입력 가능합니다.")
    String bankName,

    @Schema(description = "계좌번호", example = "12345678901234")
    @Pattern(regexp = "^[0-9]*$", message = "계좌번호는 숫자만 입력 가능합니다")
    @Size(max = 20, message = "계좌번호는 최대 20자까지 입력 가능합니다.")
    String accountNumber
) {
}
