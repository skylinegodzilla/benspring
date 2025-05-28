package com.bencawley.benspring.services;


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

@Service
public class UserService {

    // Injected dependency's
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // Other Members
    private final SecureRandom random = new SecureRandom();

    // UserService
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Register
    public UserEntity register(UserRegistrationDTO dto) {
        // Check for if email or username exists already
        if (userRepo.existsByUsername(dto.getUsername()) || userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with this username or email");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // We encode the password as soon as we get it from the json/dto
        user.setSessionToken(generateSessionToken());
        //todo generate user role
        return userRepo.save(user);
    }

    // Login
    public UserEntity login(UserLoginDTO dto) {
        UserEntity user = userRepo.findByUsername(dto.getUsername());

        if (user != null && passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) { // encode the user password and see if it matches the hash in the database
            user.setSessionToken(generateSessionToken());
            return userRepo.save(user);
        }

        return null;
    }

    // Delete Account (If Admin)
    public void deleteUserByUsernameIfAdmin(String usernameToDelete, String callerSessionToken) {
        UserEntity caller = userRepo.findBySessionToken(callerSessionToken);
        if (caller == null) {
            throw new RuntimeException("Invalid session token");
        }

        if (caller.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admins can delete users");
        }

        UserEntity targetUser = userRepo.findByUsername(usernameToDelete);
        if (targetUser == null) {
            throw new RuntimeException("User to delete not found");
        }

        userRepo.delete(targetUser);
    }

    public UserEntity findBySessionToken(String token) {
        return userRepo.findBySessionToken(token);
    }

    public UserEntity findBySessionTokenOrThrow(String token) {
        UserEntity user = findBySessionToken(token); // reuse existing method
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session token");
        }
        return user;
    }

    public UserEntity findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    private String generateSessionToken() {
        return new BigInteger(130, random).toString(32);
    }
}
