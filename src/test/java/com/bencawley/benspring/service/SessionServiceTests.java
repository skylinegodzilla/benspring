package com.bencawley.benspring.service;

import com.bencawley.benspring.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SessionServiceTests {

    private SessionService sessionService;

    @BeforeEach
    void setup() {
        sessionService = new SessionService();
    }

    @Test
    void givenUserIdAndToken_whenCreateSession_thenSessionIsStored() {
        // given
        Long userId = 42L;
        String token = "token123";

        // when
        sessionService.createSession(userId, token);

        // then
        Long storedUserId = sessionService.validateSession(token);
        assertThat(storedUserId).isEqualTo(userId);
    }

    @Test
    void givenValidToken_whenValidateSession_thenReturnsUserId() {
        // given
        Long userId = 99L;
        String token = "validToken";

        sessionService.createSession(userId, token);

        // when
        Long result = sessionService.validateSession(token);

        // then
        assertThat(result).isEqualTo(userId);
    }

    @Test
    void givenInvalidToken_whenValidateSession_thenReturnsNull() {
        // when
        Long result = sessionService.validateSession("noSuchToken");

        // then
        assertThat(result).isNull();
    }

    @Test
    void givenToken_whenInvalidateSession_thenSessionRemoved() {
        // given
        Long userId = 55L;
        String token = "tokenToRemove";

        sessionService.createSession(userId, token);
        assertThat(sessionService.validateSession(token)).isEqualTo(userId);

        // when
        sessionService.invalidateSession(token);

        // then
        assertThat(sessionService.validateSession(token)).isNull();
    }
}
