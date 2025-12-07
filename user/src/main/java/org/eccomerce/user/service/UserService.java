package org.eccomerce.user.service;

import org.eccomerce.user.dto.RegisterRequestDTO;
import org.eccomerce.user.dto.UserResponseDTO;
import org.eccomerce.user.model.User;

public interface UserService {


    UserResponseDTO getUser();

    User updateUser(Long id, User updated);

    void deleteUser(Long id);

}
