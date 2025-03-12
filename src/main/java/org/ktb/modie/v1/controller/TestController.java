package org.ktb.modie.v1.controller;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class TestController implements TestApi {

    @Override
    public int test(String code, LocalDate date) {
        return 1;
    }
}
