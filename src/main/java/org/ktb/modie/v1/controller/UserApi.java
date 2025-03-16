package org.ktb.modie.v1.controller;

import java.util.Map;

import org.ktb.modie.core.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Tag(name = "User API", description = "유저 테스트")
@Validated
@ApiResponses(
value = {
    @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공.")
})
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "사용자 프로필 조회", description = "사용자 프로필 조회 API 호출")
    @GetMapping
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getUserProfile(
    );

    @Operation(summary = "계좌번호 업데이트", description = "사용자 계좌번호 업데이트")
    @PatchMapping("/accounts")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> updateUserAccounts(
        @Parameter(description = "은행명 10자 이하")
        @RequestParam(value = "bankName", defaultValue = "국민은행")
        @Size(min = 4, max = 10)
        @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "은행명은 한글만 허용됩니다")
        @NotNull(message = "은행명은 필수입니다.")
        String bankName,

        @Parameter(description = "게장번노 20자 이하")
        @RequestParam(value = "accountNumber", defaultValue = "44020201223408")
        @Size(min = 8, max = 20, message = "계좌번호는 최대 20자리 숫자만 가능합니다.")
        @NotNull(message = "계좌번호는 필수입니다.")
        String accountNumber
    );
}
