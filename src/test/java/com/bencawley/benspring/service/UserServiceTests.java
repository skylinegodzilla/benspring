package com.bencawley.benspring.service;

import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.UserRepository;
import com.bencawley.benspring.services.UserService;
import com.bencawley.benspring.utilities.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepo = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepo, passwordEncoder);
    }

    @Test
    void givenNewUser_whenRegister_thenUserIsSavedWithEncodedPasswordAndRole() {
        // given
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("newuser");
        dto.setEmail("new@example.com");
        dto.setPassword("plainPassword");

        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0)); // just return the user passed

        // when
        UserEntity registered = userService.register(dto);

        // then
        verify(userRepo).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.getSessionToken()).isNotNull();

        assertThat(registered).isSameAs(savedUser);
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowsConflict() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("existing");
        dto.setEmail("someone@example.com");

        when(userRepo.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User already exists");
    }

    @Test
    void givenValidLogin_whenLogin_thenSessionTokenUpdatedAndReturned() {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("user1");
        dto.setPassword("password123");

        UserEntity user = new UserEntity();
        user.setUsername("user1");
        user.setPasswordHash("encodedPass");
        user.setSessionToken("oldToken");

        when(userRepo.findByUsername("user1")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        UserEntity loggedIn = userService.login(dto);

        // then
        verify(userRepo).save(user);
        assertThat(loggedIn).isNotNull();
        assertThat(loggedIn.getSessionToken()).isNotEqualTo("oldToken");
    }

    @Test
    void givenInvalidLogin_whenLogin_thenReturnsNull() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("user1");
        dto.setPassword("wrongpassword");

        UserEntity user = new UserEntity();
        user.setUsername("user1");
        user.setPasswordHash("encodedPass");

        when(userRepo.findByUsername("user1")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPass")).thenReturn(false);

        UserEntity result = userService.login(dto);

        assertThat(result).isNull();
    }

    @Test
    void givenValidSessionToken_whenFindBySessionTokenOrThrow_thenReturnsUser() {
        UserEntity user = new UserEntity();
        user.setUsername("user1");

        when(userRepo.findBySessionToken("token123")).thenReturn(user);

        UserEntity found = userService.findBySessionTokenOrThrow("token123");

        assertThat(found).isSameAs(user);
    }

    @Test
    void givenInvalidSessionToken_whenFindBySessionTokenOrThrow_thenThrowsUnauthorized() {
        when(userRepo.findBySessionToken("badtoken")).thenReturn(null);

        assertThatThrownBy(() -> userService.findBySessionTokenOrThrow("badtoken"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid session token");
    }

    // Add more as needed, but this covers the main paths
}
