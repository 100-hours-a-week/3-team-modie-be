package org.ktb.modie.presentation.v1.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Health API", description = "헬스 체크 API")
@Validated
public interface HealthCheckApi {

    @Operation(summary = "헬스 체크", description = "서버 및 DB 상태를 확인합니다.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "서버 및 DB 연결 상태 정상",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{  }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = "DB 연결 실패 또는 서버 상태 비정상",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{  }"
                    )
                )
            )
        })
    ResponseEntity<Map<String, Object>> healthCheck();
}
