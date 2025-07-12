package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.AdminUserDTO;
import com.bencawley.benspring.dtos.ChangeUserRoleRequestDTO;
import com.bencawley.benspring.dtos.MessageDTO;
import com.bencawley.benspring.dtos.PasswordResetResponseDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.AdminService;
import com.bencawley.benspring.services.SessionService;
import com.bencawley.benspring.services.UserService;
import com.bencawley.benspring.utilities.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final SessionService sessionService;

    public AdminController(UserService userService, AdminService adminService, SessionService sessionService) {
        this.userService = userService;
        this.adminService = adminService;
        this.sessionService = sessionService;
    }

    /**
     * Get a list of all users (admin only)
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        UserEntity caller = userService.findBySessionTokenOrThrow(token);
        return ResponseEntity.ok(adminService.getAllUsers(caller));
    }

    /**
     * Delete a user by username (admin only)
     */
    @DeleteMapping("/remove/{username}")
    public ResponseEntity<MessageDTO> deleteUser(@PathVariable String username, @RequestHeader("Authorization") String token) {
        try {
            UserEntity caller = userService.findBySessionTokenOrThrow(token);
            adminService.deleteUser(username, caller);
            return ResponseEntity.ok(new MessageDTO("User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageDTO(e.getMessage()));
        }
    }

    /**
     * Promote a user to admin (admin only)
     */
    @PatchMapping("/users/{username}/role")
    public ResponseEntity<MessageDTO> changeUserRole(
            @PathVariable String username,
            @RequestHeader("Authorization") String token,
            @RequestBody ChangeUserRoleRequestDTO request
    ) {
        try {
            UserEntity caller = userService.findBySessionTokenOrThrow(token);
            UserRole newRole = UserRole.valueOf(request.getNewRole().toUpperCase());
            adminService.changeUserRole(username, caller, newRole);
            return ResponseEntity.ok(new MessageDTO("User role updated to " + newRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Invalid role."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageDTO(e.getMessage()));
        }
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<AdminUserDTO> getUserByUsername(
            @PathVariable String username,
            @RequestHeader("Authorization") String token
    ) {
        UserEntity caller = userService.findBySessionTokenOrThrow(token);
        return ResponseEntity.ok(adminService.getUserByUsername(username, caller));
    }

    @PostMapping("/users/{username}/reset-password")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(
            @PathVariable String username,
            @RequestHeader("Authorization") String token
    ) {
        UserEntity caller = userService.findBySessionTokenOrThrow(token);
        String newPassword = adminService.resetUserPassword(username, caller);
        return ResponseEntity.ok(new PasswordResetResponseDTO(
                "Password reset successfully. Show this password to the user immediately.",
                newPassword
        ));
    }

}
