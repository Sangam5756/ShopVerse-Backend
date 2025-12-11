package org.ecommerce.user.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.ecommerce.user.dto.LoginRequest;
import org.ecommerce.user.dto.RegisterRequestDTO;
import org.ecommerce.user.dto.TokenResponseDTO;
import org.ecommerce.user.dto.UserResponseDTO;
import org.ecommerce.user.mapper.UserMapper;
import org.ecommerce.user.model.User;
import org.ecommerce.user.repository.UserRepository;
import org.ecommerce.user.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

//        first check convert dto to entity
        User newUser = new User();
        newUser.setFullName(dto.getFullName());
        newUser.setEmail(dto.getEmail());
        newUser.setPhoneNo(dto.getPhoneNo());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));


        User saved = userRepository.save(newUser);
        return UserMapper.mapToDto(saved);

    }

    @Override
    public TokenResponseDTO login(LoginRequest dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new TokenResponseDTO(token);
    }
}

