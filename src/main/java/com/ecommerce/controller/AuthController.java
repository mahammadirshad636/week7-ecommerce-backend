package com.ecommerce.controller;

import com.ecommerce.model.dto.AuthRequest;
import com.ecommerce.model.dto.AuthResponse;
import com.ecommerce.model.dto.UserDTO;
import com.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        UserDTO user = UserDTO.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getRole())
                .build();
        return ResponseEntity.ok(userService.register(user, request.getPassword()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    public static class RegisterRequest {
        private String email;
        private String password;
        private String name;
        private com.ecommerce.model.enums.Role role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public com.ecommerce.model.enums.Role getRole() { return role; }
        public void setRole(com.ecommerce.model.enums.Role role) { this.role = role; }
    }
}
