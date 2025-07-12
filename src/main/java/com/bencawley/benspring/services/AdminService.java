package com.bencawley.benspring.services;

import com.bencawley.benspring.dtos.AdminUserDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.utilities.UserRole;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class AdminService {

    private final UserService userService;

    public AdminService(UserService userService) {
        this.userService = userService;
    }

    public List<AdminUserDTO> getAllUsers(UserEntity caller) {
        assertIsAdmin(caller);
        return userService.getAllUsers();
    }

    public AdminUserDTO getUserByUsername(String username, UserEntity caller) {
        assertIsAdmin(caller);
        UserEntity user = userService.findByUsernameOrThrow(username);
        return new AdminUserDTO(user.getUsername(), user.getEmail(), user.getRole());
    }

    public void deleteUser(String targetUsername, UserEntity caller) {
        assertIsAdmin(caller);
        if (caller.getUsername().equals(targetUsername)) {
            throw new RuntimeException("You can't delete yourself.");
        }
        UserEntity target = userService.findByUsernameOrThrow(targetUsername);
        userService.delete(target);
    }

    public void changeUserRole(String targetUsername, UserEntity caller, UserRole newRole) {
        assertIsAdmin(caller);
        if (caller.getUsername().equals(targetUsername) && newRole != UserRole.ADMIN) {
            throw new RuntimeException("You can't demote yourself.");
        }

        UserEntity target = userService.findByUsernameOrThrow(targetUsername);
        target.setRole(newRole);
        userService.save(target);
    }

    public String resetUserPassword(String username, UserEntity caller) {
        assertIsAdmin(caller);
        UserEntity target = userService.findByUsernameOrThrow(username);

        String newPassword = generateSecurePassword();
        String hashed = userService.encodePassword(newPassword);

        target.setPasswordHash(hashed);
        userService.save(target);

        return newPassword; // Returned once to the controller only
    }

    private String generateSecurePassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void assertIsAdmin(UserEntity user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admins can perform this action.");
        }
    }
}
