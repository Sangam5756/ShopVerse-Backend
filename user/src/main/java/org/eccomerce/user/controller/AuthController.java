package org.eccomerce.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.eccomerce.user.dto.LoginRequest;
import org.eccomerce.user.dto.RegisterRequestDTO;
import org.eccomerce.user.dto.TokenResponseDTO;
import org.eccomerce.user.service.AuthService;
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
