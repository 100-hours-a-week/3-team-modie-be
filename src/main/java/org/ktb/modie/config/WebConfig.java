package org.ktb.modie.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * !!할일 : 프로트엔드 포트에 맞게 CORS 설정 수정 필요 !
 * author : hyuk.kim (김상혁)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173", "http://localhost:8080", "https://modie.site", "https://dev.modie.site", "http://dev.modie.site", "https://dev-api.modie.site", "http://dev-api.modie.site")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("Accept", "Content-Type", "Authorization")
            .exposedHeaders("Content-Type")
            .allowCredentials(true)
            .maxAge(3600);
    }

}
