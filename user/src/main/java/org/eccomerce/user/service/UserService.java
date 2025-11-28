package org.eccomerce.user.service;

import org.eccomerce.user.model.User;

public interface UserService {

    User registerUser(User user);

    User getUserById(Long id);

    User updateUser(Long id, User updated);

    void deleteUser(Long id);
}
