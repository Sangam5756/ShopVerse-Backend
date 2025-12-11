package org.ecommerce.user.service;

import org.ecommerce.user.dto.UserResponseDTO;
import org.ecommerce.user.model.User;

public interface UserService {


    UserResponseDTO getUser();

    User updateUser(Long id, User updated);

    void deleteUser(Long id);

}
