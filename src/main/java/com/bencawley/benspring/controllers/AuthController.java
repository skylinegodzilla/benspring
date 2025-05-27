package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.UserDTO;
import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.SessionService;
import com.bencawley.benspring.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, SessionService sessionService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;  // Initialize here
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO dto) {
        UserEntity user = userService.register(dto);
        return ResponseEntity.ok(user.getSessionToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        UserEntity user = userService.findByUsername(dto.getUsername());

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = sessionService.createSession(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }
}
