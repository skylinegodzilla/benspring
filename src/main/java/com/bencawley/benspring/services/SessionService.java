package com.bencawley.benspring.services;


import com.bencawley.benspring.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    // Map of session token -> userId
    private static final Map<String, Long> sessions = new ConcurrentHashMap<>();

    // Create a new session for a user
    public void createSession(Long userId, String token) {
        sessions.put(token, userId);
        System.out.println("Created valid session for:" + userId); //todo remove this line
    }

    // Validate a session token and return the associated userId if valid, else null
    public Long validateSession(String token) {
        System.out.println("Validating session token:" + token); //todo remove this line
        System.out.println("UserID for the token is:" + sessions.get(token)); //todo remove this line
        System.out.println("All sessions:" + sessions); //todo remove this line
        return sessions.get(token);
    }

    // Invalidate a session token
    public void invalidateSession(String token) {
        sessions.remove(token);
    }

    // todo add this
    //UserEntity getUserBySession(String token); // Optional, helpful for cleaner controller code
}
