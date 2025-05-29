package com.bencawley.benspring.startup;

import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.UserRepository;
import com.bencawley.benspring.services.SessionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionStartupLoader {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void loadSessions() {
        for (UserEntity user : userRepository.findAll()) {
            String token = user.getSessionToken();
            if (token != null && !token.isBlank()) {
                sessionService.createSession(user.getId(), token);
                System.out.println("Restored session: " + token + " for userID: " + user.getId()); //todo: maybe remove the token out of the log.
            }
        }
    }
}

