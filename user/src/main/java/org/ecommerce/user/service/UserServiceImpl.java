package org.ecommerce.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecommerce.user.dto.UserResponseDTO;
import org.ecommerce.user.mapper.UserMapper;
import org.ecommerce.user.model.User;
import org.ecommerce.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder  passwordEncoder;





    @Override
    public UserResponseDTO getUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println(email);
        User existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
       existUser.getAddresses().size();
       return UserMapper.mapToDto(existUser);

    }

    @Override
    public User updateUser(Long id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setFullName(updated.getFullName());

//        change the email if only its need
        if (!existing.getEmail().equals(updated.getEmail())) {
            if (userRepository.existsByEmail(updated.getEmail()))
                throw new RuntimeException("Email already in use");

            existing.setEmail(updated.getEmail());
        }

        existing.setPhoneNo(updated.getPhoneNo());
        existing.setRole(updated.getRole());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User existing =userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(existing);
    }
}
