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
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("x-api-key", "Accept")
                .exposedHeaders("Content-Type")
                .allowCredentials(false)
                .maxAge(3600);
    }

}
