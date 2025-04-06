package com.practice.blockingio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiTestService {
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // Blocking IO
    public String callApiBlocking() {
        Instant start = Instant.now();

        String response = restTemplate.getForObject("https://httpbin.org/delay/1", String.class);

        long elapsedTime = Duration.between(start, Instant.now()).toMillis();
        return "Blocking API 호출 완료: " + elapsedTime + "ms (응답 길이: " +
                (response != null ? response.length() : 0) + ")";
    }

    // 요청 시간 확인
    public void executeBlockingRequests() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("# 요청 시작 시간: {}", startTime.format(formatter));

        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep(5000);

                LocalDateTime responseTime = LocalDateTime.now();
                log.info("{}: API CALL : CALL TIME {}", responseTime.format(formatter), i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted", e);
            }
        }
    }

    public void executeRealBlockingRequests() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("# 요청 시작 시간: {}", startTime.format(formatter));

        // 5개의 실제 API 요청을 순차적으로 처리
        for (int i = 1; i <= 5; i++) {
            String response = restTemplate.getForObject("https://httpbin.org/delay/5", String.class);

            LocalDateTime responseTime = LocalDateTime.now();
            log.info("{}: 스레드 {} (응답 길이: {})",
                    responseTime.format(formatter),
                    i,
                    response != null ? response.length() : 0);
        }
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

    public Mono<Void> executeNonBlockingRequests() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("# 요청 시작 시간: {}", startTime.format(formatter));

        return Flux.range(1, 5)
                .flatMap(i -> {
                    // 5초 지연을 가진 가상의 비동기 작업
                    return Mono.delay(Duration.ofSeconds(5))
                            .map(ignored -> {
                                LocalDateTime responseTime = LocalDateTime.now();
                                log.info("{}: 시간 : 스레드 {}",
                                        responseTime.format(formatter), i);
                                return i;
                            });
                })
                .then();
    }

}
