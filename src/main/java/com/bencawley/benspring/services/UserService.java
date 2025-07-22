package com.bencawley.benspring.services;


import com.bencawley.benspring.dtos.AdminUserDTO;
import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.UserRepository;
import com.bencawley.benspring.utilities.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Register
    public UserEntity register(UserRegistrationDTO dto) {
        if (userRepo.existsByUsername(dto.getUsername()) || userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with this username or email");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setSessionToken(generateSessionToken());
        user.setRole(UserRole.USER); // <- Now initialized properly
        return userRepo.save(user);
    }

    // Login
    public UserEntity login(UserLoginDTO dto) {
        UserEntity user = userRepo.findByUsername(dto.getUsername());
        if (user != null && passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            user.setSessionToken(generateSessionToken());
            return userRepo.save(user);
        }
        return null;
    }

    // Core user operations
    public UserEntity findBySessionToken(String token) {
        return userRepo.findBySessionToken(token);
    }

    public UserEntity findBySessionTokenOrThrow(String token) {
        UserEntity user = findBySessionToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session token");
        }
        return user;
    }

    public UserEntity findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public UserEntity findByUsernameOrThrow(String username) {
        UserEntity user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public UserEntity getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public UserEntity save(UserEntity user) {
        return userRepo.save(user);
    }

    /**
     * For admin use only – callers must check admin permissions.
     * Use only from AdminService.
     */
    public void delete(UserEntity user) {
        userRepo.delete(user);
    }

    /**
     * For admin use only – callers must check admin permissions.
     * Use only from AdminService.
     */
    public List<AdminUserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(user -> new AdminUserDTO(
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private String generateSessionToken() {
        return new BigInteger(130, random).toString(32);
    }
}
