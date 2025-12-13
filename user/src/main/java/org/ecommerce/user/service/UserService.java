package org.ecommerce.user.service;

import org.ecommerce.user.dto.UserCreateRequest;
import org.ecommerce.user.dto.UserResponse;

public interface UserService {

    UserResponse getUserById(String email);

    UserResponse getUserByEmail(String email);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateProfile(String email, UserCreateRequest request);

    void deleteUser(String email);
}
