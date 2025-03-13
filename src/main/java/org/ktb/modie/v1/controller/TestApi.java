package org.ktb.modie.v1.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Tag(name = "스웨거 테스트", description = "스웨거를 테스트")
@Validated
public interface TestApi {

    @Operation(summary = "스웨거 테스트 API", description = "스웨거 테스트")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 스웨거가 보였습니다.")

    })
    @GetMapping(value = "/api/v1/test/{code}")
    int test(
        @Parameter(description = "코드 (최대 10자리)")
        @PathVariable("code")
        @Size(max = 10, message = "코드는 10자리 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "코드는 영문 대문자와 숫자만 허용됩니다")
        String code,

        @Parameter(description = "시작 날짜 (yyyy-MM-dd)")
        @RequestParam("date")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "날짜는 필수입니다")
        @Past(message = "날짜는 과거 날짜여야 합니다")
        LocalDate date
    );

}
