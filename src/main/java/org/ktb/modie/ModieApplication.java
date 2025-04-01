package org.ktb.modie;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ModieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModieApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // JVM 전체 타임존을 KST로 고정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("✅ JVM 타임존 설정됨: " + TimeZone.getDefault().getID()); // 확인 로그
    }
}
