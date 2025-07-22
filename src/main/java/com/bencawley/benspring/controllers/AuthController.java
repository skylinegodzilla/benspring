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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          SessionService sessionService
    ) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    /**
     * Registers a new user and creates a session token.
     *
     * @param dto User registration details (email, password, etc.)
     * @return Response with session token and success message
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@RequestBody UserRegistrationDTO dto) {
        // todo this will accept any correct formatted response so you might want to have a valid email check...
        // also might want to add a check for password and confirm password match
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

    /**
     * Logs in a user with provided credentials and returns a session token.
     *
     * @param dto User login credentials
     * @return Session token and user role, or unauthorized status
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginDTO dto) {
        try {
            UserEntity user = userService.login(dto);

            if (user == null) {
                UserLoginResponseDTO errorResponse = new UserLoginResponseDTO();
                errorResponse.setToken(null);
                errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                errorResponse.setMessage("Invalid credentials");
                errorResponse.setRole(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            sessionService.createSession(user.getId(), user.getSessionToken());

            UserLoginResponseDTO response = new UserLoginResponseDTO();
            response.setToken(user.getSessionToken());
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login successful");
            response.setRole(user.getRole());

            log.info("Response for user role: {}", user.getRole()); // TODO: debugging remove when done

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            UserLoginResponseDTO errorResponse = new UserLoginResponseDTO();
            errorResponse.setToken(null);
            errorResponse.setStatus(ex.getStatusCode().value());
            errorResponse.setMessage(ex.getReason());
            errorResponse.setRole(null);
            return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
        }
    }

    /**
     * Logs out the user by invalidating their session token.
     *
     * @param token Session token from Authorization header
     * @return Logout success or error response
     */
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
}
