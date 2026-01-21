package com.ecommerce.auth.client;

import com.ecommerce.auth.dto.ErrorResponse;
import com.ecommerce.auth.dto.InternalUserAuthResponse;
import com.ecommerce.auth.dto.UserRegistrationRequest;
import com.ecommerce.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class  UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String USER_SERVICE = "http://USER-SERVICE";

    public Mono<InternalUserAuthResponse> getUserForAuth(String email) {
        return webClientBuilder.build()
                .get()
                .uri(USER_SERVICE + "/api/internal/users/auth/{email}", email)
                .retrieve()
                .bodyToMono(InternalUserAuthResponse.class);
    }

    public Mono<UserResponse> createUser(UserRegistrationRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(USER_SERVICE + "/api/internal/users")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}

