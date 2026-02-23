package com.ecommerce.service;

import com.ecommerce.model.dto.AuthRequest;
import com.ecommerce.model.dto.AuthResponse;
import com.ecommerce.model.dto.UserDTO;
import com.ecommerce.model.entity.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDTO register(UserDTO request, String rawPassword) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(rawPassword)
                .name(request.getName())
                .role(request.getRole() == null ? Role.USER : request.getRole())
                .build();

        return toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name(), "Login successful");
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserDTO updateProfile(Long id, UserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(request.getName());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return toDto(userRepository.save(user));
    }

    private UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
