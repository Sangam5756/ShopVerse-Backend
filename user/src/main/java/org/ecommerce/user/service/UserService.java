package org.ecommerce.user.service;

import org.ecommerce.user.dto.UserCreateRequest;
import org.ecommerce.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserByEmail(String email);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateProfile(String email, UserCreateRequest request);

    UserResponse deleteUser(String email);

    List<UserResponse> getAllUsers();

    // Password reset methods
    String createPasswordResetToken(String email);

    boolean validateResetToken(String token);

    UserResponse resetPassword(String token, String newPassword);

    UserResponse findByResetToken(String token);
}
