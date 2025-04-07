package org.ktb.modie;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ModieApplication {
    private static final Logger log = LoggerFactory.getLogger(ModieApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ModieApplication.class, args);

        log.info("Current Directory: {}", System.getProperty("user.dir"));
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
