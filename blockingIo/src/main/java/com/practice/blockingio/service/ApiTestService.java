package com.practice.blockingio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiTestService {
    private final RestTemplate restTemplate;
    private final WebClient webClient;

    // Blocking IO
    public String callApiBlocking() {
        Instant start = Instant.now();

        String response = restTemplate.getForObject("https://httpbin.org/delay/1", String.class);

        long elapsedTime = Duration.between(start, Instant.now()).toMillis();
        return "Blocking API 호출 완료: " + elapsedTime + "ms (응답 길이: " +
                (response != null ? response.length() : 0) + ")";
    }

    // Non-Blocking IO
    public Mono<String> callApiNonBlocking() {
        Instant start = Instant.now();

        // WebClient는 비동기식(Non-Blocking) HTTP 클라이언트
        return webClient.get()
                .uri("/delay/1")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    long elapsedTime = Duration.between(start, Instant.now()).toMillis();
                    return "Non-Blocking API 호출 완료: " + elapsedTime + "ms (응답 길이: " +
                            response.length() + ")";
                });
    }
    //병렬로 실행
    public Mono<String> callMultipleApisInParallel() {
        Instant start = Instant.now();

        // 여러 API를 동시에 호출
        Mono<String> call1 = webClient.get().uri("/delay/1").retrieve().bodyToMono(String.class);
        Mono<String> call2 = webClient.get().uri("/delay/1").retrieve().bodyToMono(String.class);
        Mono<String> call3 = webClient.get().uri("/delay/1").retrieve().bodyToMono(String.class);

        return Mono.zip(call1, call2, call3)
                .map(responses -> {
                    long elapsedTime = Duration.between(start, Instant.now()).toMillis();
                    return "3개의 병렬 API 호출 완료: " + elapsedTime + "ms";
                });
    }
}
