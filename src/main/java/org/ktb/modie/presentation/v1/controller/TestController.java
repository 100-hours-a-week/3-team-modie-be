package org.ktb.modie.presentation.v1.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {

    @Override
    public int test(String code, LocalDate date) {
        return 1;
    }
}
