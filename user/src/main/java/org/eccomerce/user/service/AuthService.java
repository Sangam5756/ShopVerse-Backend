package org.eccomerce.user.service;

import org.eccomerce.user.dto.LoginRequest;
import org.eccomerce.user.dto.RegisterRequestDTO;
import org.eccomerce.user.dto.TokenResponseDTO;
import org.eccomerce.user.dto.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(RegisterRequestDTO dto);
    TokenResponseDTO login(LoginRequest dto);
}
