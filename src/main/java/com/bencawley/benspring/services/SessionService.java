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
        System.out.println("Created valid session for User:" + userId);
    }

    // Validate a session token and return the associated userId if valid, else null
    public Long validateSession(String token) {
        return sessions.get(token);
    }

    // Invalidate a session token
    public void invalidateSession(String token) {
        sessions.remove(token);
    }
}
