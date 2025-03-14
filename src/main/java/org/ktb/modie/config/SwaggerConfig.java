package org.ktb.modie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            .url("http://localhost:8080")
            .description("개발용 서버");

        return new OpenAPI()
            .info(info)
            .addServersItem(devServer);
    }

    /*
     * !!할일 : API 구현 후 그룹화 진행 필요!!
     * author : hyuk.kim (김상혁)
     */
    @Bean
    public GroupedOpenApi stockV1Api() {
        return GroupedOpenApi.builder()
            .group("예제용 API")
            .pathsToMatch("/api/v1/test/**")
            .packagesToScan("org.ktb.modie.v1.controller")
            .build();
    }

    @Bean
    public GroupedOpenApi stockV2Api() {
        return GroupedOpenApi.builder()
            .group("예제용 API v2")
            .pathsToMatch("/api/v1/test/**")
            .packagesToScan("org.ktb.modie.v1.controller")
            .build();
    }
}
