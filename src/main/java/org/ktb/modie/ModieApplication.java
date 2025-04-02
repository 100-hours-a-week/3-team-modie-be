package org.ktb.modie;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ModieApplication {
    private static final Logger logger = LoggerFactory.getLogger(ModieApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ModieApplication.class, args);
        // 현재 디렉토리 출력
        logger.info("현재 디렉토리: {}", System.getProperty("user.dir"));

        // 로그 설정 확인
        logger.info("로그 메시지 테스트");
        logger.debug("로그 디버그 메시지 테스트");
        logger.error("로그 에러 메시지 테스트");
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
