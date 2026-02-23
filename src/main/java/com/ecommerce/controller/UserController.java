package com.ecommerce.controller;

import com.ecommerce.model.dto.UserDTO;
import com.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> profile(@RequestParam Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestParam Long id, @RequestBody UserDTO request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }
}
