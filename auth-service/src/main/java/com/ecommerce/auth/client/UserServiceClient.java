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
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final String userServiceUrl = "http://USER-SERVICE";

    public Mono<InternalUserAuthResponse> getUserForAuth(String email) {
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/api/internal/users/auth/{email}", email)
                .retrieve()
                .bodyToMono(InternalUserAuthResponse.class);
    }

    public Mono<UserResponse> createUser(UserRegistrationRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://USER-SERVICE/api/internal/users")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(ErrorResponse.class)
                                .flatMap(error -> Mono.error(
                                        new ResponseStatusException(
                                                response.statusCode(),
                                                error.getMessage()
                                        )
                                ))
                )
                .bodyToMono(UserResponse.class);
    }

}
