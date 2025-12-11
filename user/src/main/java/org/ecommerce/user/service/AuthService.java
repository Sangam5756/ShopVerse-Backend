package org.ecommerce.user.service;

import org.ecommerce.user.dto.LoginRequest;
import org.ecommerce.user.dto.RegisterRequestDTO;
import org.ecommerce.user.dto.TokenResponseDTO;
import org.ecommerce.user.dto.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(RegisterRequestDTO dto);
    TokenResponseDTO login(LoginRequest dto);
}
