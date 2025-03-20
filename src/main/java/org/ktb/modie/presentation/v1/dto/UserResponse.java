package org.ktb.modie.presentation.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "사용자 프로필 조회")
@Builder
public record UserResponse(
    @Schema(description = "유저아이디", defaultValue = "12345")
    String userId,
    @Schema(description = "유저이름", defaultValue = "urungLee")
    String userName,
    @Schema(description = "프로필사진")
    String profileImageUrl,
    @Schema(description = "은행명")
    String bankName,
    @Schema(description = "계좌번호")
    String accountNumber
) {
}
