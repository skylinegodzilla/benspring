package com.bencawley.benspring.services;


import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.math.BigInteger;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserEntity register(UserRegistrationDTO dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword()); // ← just for now
        /*
            todo: replace the line above with this once you are able to inject a PasswordEncoder (like Spring Security’s BCryptPasswordEncoder) else you are going to store passwords in plain text
            String hashedPassword = passwordEncoder.encode(dto.getPassword());
            user.setPasswordHash(hashedPassword);
         */
        user.setSessionToken(generateSessionToken());
        return userRepo.save(user);
    }

    public UserEntity login(UserLoginDTO dto) {
        UserEntity user = userRepo.findByUsername(dto.getUsername());

        if (user != null && user.getPasswordHash().equals(dto.getPassword())) { // ← just for now
            /*
                todo: replace the line above with this once you are able to inject a PasswordEncoder
                if (user != null && passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
             */
            String token = generateSessionToken();
            user.setSessionToken(token);
            return userRepo.save(user); // Save updated token
        }

        return null;
    }

    public UserEntity findBySessionToken(String token) {
        return userRepo.findBySessionToken(token);
    }

    public UserEntity findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    private String generateSessionToken() {
        return new BigInteger(130, random).toString(32);
    }
}
