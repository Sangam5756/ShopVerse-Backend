package org.ecommerce.user.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.user.dto.InternalUserAuthResponse;
import org.ecommerce.user.dto.UserCreateRequest;
import org.ecommerce.user.dto.UserResponse;
import org.ecommerce.user.model.User;
import org.ecommerce.user.repository.UserRepository;
import org.ecommerce.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/auth/{email}")
    public InternalUserAuthResponse getUserForAuth(@PathVariable String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        InternalUserAuthResponse dto = new InternalUserAuthResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setFullName(user.getFullName());

        return dto;
    }
}
