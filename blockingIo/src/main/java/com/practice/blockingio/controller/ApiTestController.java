package com.practice.blockingio.controller;

import com.practice.blockingio.service.ApiTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api-test")
@RequiredArgsConstructor
public class ApiTestController {

    private final ApiTestService apiTestService;

    @GetMapping("/blocking")
    public String testApiBlocking() {
        return apiTestService.callApiBlocking();
    }

    @GetMapping("/non-blocking")
    public Mono<String> testApiNonBlocking() {
        return apiTestService.callApiNonBlocking();
    }

    @GetMapping("/parallel")
    public Mono<String> testApiParallel() {
        return apiTestService.callMultipleApisInParallel();
    }
}

