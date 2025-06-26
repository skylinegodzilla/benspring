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
     * @return Session token and user ID, or unauthorized status
     */
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

    /**
     * Deletes a user account by username, only if the requester is an admin.
     *
     * @param username Username of the user to delete
     * @param sessionToken Admin's session token from Authorization header
     * @return Success or failure message
     */
    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> deleteUser( // todo fix this response entity to return a real object
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
