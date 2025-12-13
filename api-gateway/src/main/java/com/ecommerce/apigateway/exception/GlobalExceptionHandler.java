package com.ecommerce.apigateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Set the HTTP status
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Prepare error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : "Authentication failed");
        errorResponse.put("path", exchange.getRequest().getPath().toString());

        // Convert the error response to JSON
        String responseBody = "{\"status\":" + HttpStatus.UNAUTHORIZED.value() + 
                            ",\"error\":\"Unauthorized\"" +
                            ",\"message\":\"" + (ex.getMessage() != null ? ex.getMessage() : "Authentication failed") + "\"" +
                            ",\"path\":\"" + exchange.getRequest().getPath().toString() + "\"}";

        // Write the response
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
