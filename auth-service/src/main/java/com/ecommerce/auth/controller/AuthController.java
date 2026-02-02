package com.ecommerce.auth.controller;

import com.ecommerce.auth.client.UserServiceClient;
import com.ecommerce.auth.dto.AuthRequest;
import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.ForgotPasswordRequest;
import com.ecommerce.auth.dto.PasswordResetResponse;
import com.ecommerce.auth.dto.ResetPasswordRequest;
import com.ecommerce.auth.dto.UserRegistrationRequest;
import com.ecommerce.auth.dto.UserResponse;
import com.ecommerce.auth.producer.AuthEventPublisher;
import com.ecommerce.auth.security.CustomUserDetailsService;
import com.ecommerce.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;
    private final AuthEventPublisher authEventPublisher;


    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest request) {

        return authService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtTokenProvider.generateToken(
                            user.getEmail(),
                            user.getFullName(),
                            user.getRole()
                    );

                    // ✅ SEND ROLE ALSO
                    authEventPublisher.userLoggedIn(
                            user.getEmail()
                    );

                    return new AuthResponse(token, "Bearer");
                })
                .onErrorResume(e ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                e.getMessage()
                        ))
                );
    }


    @PostMapping("/register")
    public Mono<UserResponse> register(@RequestBody UserRegistrationRequest request) {

        return userServiceClient.createUser(request)
                .doOnNext(user ->
                        authEventPublisher.userRegistered(user.getEmail())
                );
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PasswordResetResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userServiceClient.createPasswordResetToken(request.getEmail())
                .map(token -> {
                    // Publish password reset event to send email notification
                    authEventPublisher.passwordResetRequested(request.getEmail(), token);
                    return new PasswordResetResponse("Password reset link sent to your email", true);
                })
                .onErrorResume(e -> {
                    // Always return success to prevent email enumeration attacks
                    return Mono.just(new PasswordResetResponse("If your email exists, a reset link has been sent", true));
                });
    }

    @PostMapping("/reset-password")
    public Mono<PasswordResetResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return userServiceClient.resetPassword(request.getToken(), request.getNewPassword())
                .map(user -> new PasswordResetResponse("Password reset successfully", true))
                .onErrorResume(e -> Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                )));
    }
}
