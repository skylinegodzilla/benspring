package com.bencawley.benspring.services;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    // Map of session token -> userId
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    // Create a new session token for a user
    public String createSession(Long userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
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
