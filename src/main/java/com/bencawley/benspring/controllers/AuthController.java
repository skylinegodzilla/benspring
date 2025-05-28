package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.UserDTO;
import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.dtos.UserRegistrationResponseDTO;
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
    public ResponseEntity<UserRegistrationResponseDTO> register(@RequestBody UserRegistrationDTO dto) { // todo might pay to rember this will accept any correctlery formated response so you might want to have an valid email check.. also might also want to add in a check for password and conform password and that they match
        UserEntity user = userService.register(dto);
        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO(
                user.getSessionToken(),
                HttpStatus.OK.value(),
                "Registration successful"
        );
        return ResponseEntity.ok(response);
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

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String username,
            @RequestHeader("Authorization") String sessionToken) {
        try {
            userService.deleteUserByUsernameIfAdmin(username, sessionToken);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "User deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", 403,
                    "message", e.getMessage()
            ));
        }
    }
}
