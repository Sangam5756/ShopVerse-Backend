package com.ecommerce.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return createFallbackResponse("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/discovery-service")
    public Mono<ResponseEntity<Map<String, Object>>> discoveryServiceFallback() {
        return createFallbackResponse("Service Discovery is currently unavailable. Please try again later.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "error", "Service Unavailable",
                        "message", message
                )));
    }
}
