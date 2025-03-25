package org.ktb.modie.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    /*
     * MODiE API 문서
     * 버전 : v1
     * 서버 : localhost:8080
     * 설명 : 개발용 서버
     */
    @Bean
    public OpenAPI openApi() {
        Info info = new Info()
            .title("MODiE API 문서")
            .version("v1")
            .description("API 명세서");

        Server devServer = new Server()
            .url("https://dev-api.modie.site")
            .description("개발용 서버");

        return new OpenAPI()
            .info(info)
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 인증 토큰을 입력하세요 (Bearer 접두사 없이)")
                )
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("bearerAuth"))
            .addServersItem(devServer);
    }

    /*
     * 예제용 API (기존 API 그룹)
     * author : hyuk.kim (김상혁)
     */

    @Bean
    public GroupedOpenApi meetApi() {
        return GroupedOpenApi.builder()
            .group("API")
            .pathsToMatch("/api/v1/**")
            .packagesToScan("org.ktb.modie.presentation.v1.controller")
            .build();
    }

    /*
     * 웹소켓 API 그룹화
     * author : jade.lee (이태현)
     */
    @Bean
    public GroupedOpenApi chatSendV1Api() {
        return GroupedOpenApi.builder()
            .group("웹소켓 API")
            .pathsToMatch("/ws/**") // WebSocket 엔드포인트 경로
            .packagesToScan("org.ktb.modie.presentation.v1.controller") // WebSocket 관련 컨트롤러 포함
            .build();
    }
    /*
     * health check API (기존 API 그룹)
     * author : urung.lee (이우형)
     */

    @Bean
    public GroupedOpenApi healthCheckApi() {
        return GroupedOpenApi.builder()
            .group("Health Check API")
            .pathsToMatch("/health")
            .packagesToScan("org.ktb.modie.presentation.v1.controller")
            .build();
    }

}
