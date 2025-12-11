package org.ecommerce.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.ecommerce.user.dto.LoginRequest;
import org.ecommerce.user.dto.RegisterRequestDTO;
import org.ecommerce.user.dto.TokenResponseDTO;
import org.ecommerce.user.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
