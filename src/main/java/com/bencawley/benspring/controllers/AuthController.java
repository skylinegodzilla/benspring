package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.*;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.SessionService;
import com.bencawley.benspring.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          SessionService sessionService
    ) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@RequestBody UserRegistrationDTO dto) { // todo this will accept any correct formated response so you might want to have an valid email check... also might also want to add in a check for password and conform password and that they match
        UserEntity user = userService.register(dto);

        // Don't forget to create the session LOL
        sessionService.createSession(user.getId(), user.getSessionToken());

        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO(
                user.getSessionToken(),
                HttpStatus.OK.value(),
                "Registration successful"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginDTO dto) {
        try {
            UserEntity user = userService.login(dto);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new UserLoginResponseDTO(null,
                                null,
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid credentials"
                        )
                );
            }
            // Don't forget to create the session LOL
            sessionService.createSession(user.getId(), user.getSessionToken());

            UserLoginResponseDTO response = new UserLoginResponseDTO(
                    user.getSessionToken(),
                    user.getId(),
                    HttpStatus.OK.value(),
                    "Login successful"
            );

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(
                    new UserLoginResponseDTO(
                            null,
                            null,
                            ex.getStatusCode().value(),
                            ex.getReason()
                    )
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogOutResponseDTO> logout(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            LogOutResponseDTO response = new LogOutResponseDTO();
            response.setSuccess(false);
            response.setMessage("Missing session token.");
            return ResponseEntity.badRequest().body(response);
        }

        Long userId = sessionService.validateSession(token);
        if (userId == null) {
            LogOutResponseDTO response = new LogOutResponseDTO();
            response.setSuccess(false);
            response.setMessage("Invalid session token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        UserEntity user = userService.getUserById(userId);
        if (user != null) {
            user.setSessionToken(null);
            userService.save(user);
        }

        sessionService.invalidateSession(token);

        LogOutResponseDTO response = new LogOutResponseDTO();
        response.setSuccess(true);
        response.setMessage("Logged out successfully.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> deleteUser( // todo fix this response entity to return an real object
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
