package com.ecommerce.auth.security;

import com.ecommerce.auth.client.UserServiceClient;
import com.ecommerce.auth.dto.InternalUserAuthResponse;
import com.ecommerce.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;

    public Mono<InternalUserAuthResponse> authenticate(String email, String rawPassword) {

        return userServiceClient.getUserForAuth(email)
                .flatMap(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                    return Mono.just(user);
                });
    }
}
