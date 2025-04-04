package org.ktb.modie.presentation.v1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController implements ExampleApi {

    private static final Logger log = LoggerFactory.getLogger(ExampleController.class);

    @Override
    public ResponseEntity<String> example() {
        log.debug("디버그 메시지 - local 환경에서만 보임");
        log.info("정보 메시지 - local, dev 환경에서 보임");
        log.warn("경고 메시지 - 모든 환경에서 보임");

        try {
            // 비즈니스 로직 수행
            if (Math.random() > 0.8) {
                throw new RuntimeException("테스트 예외 발생");
            }
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An error occurred");
        }

        return ResponseEntity.ok("Hello World");
    }
}
