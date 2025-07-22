package com.bencawley.benspring.controller;

import com.bencawley.benspring.controllers.AuthController;
import com.bencawley.benspring.dtos.UserLoginDTO;
import com.bencawley.benspring.dtos.UserRegistrationDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.SessionService;
import com.bencawley.benspring.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private SessionService sessionService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        AuthController controller = new AuthController(userService, passwordEncoder, sessionService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("givenValidRegistrationDto_whenRegister_thenReturnsSuccess")
    void givenValidRegistrationDto_whenRegister_thenReturnsSuccess() throws Exception {
        // given
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        UserEntity saved = new UserEntity();
        saved.setId(1L);
        saved.setSessionToken("mock-token");

        // use lenient stub to avoid strict stubbing errors
        lenient().when(userService.register(any(UserRegistrationDTO.class))).thenReturn(saved);

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.message").value("Registration successful"));

        verify(sessionService).createSession(1L, "mock-token");
    }

    @Test
    @DisplayName("givenValidLoginDto_whenLogin_thenReturnsTokenAndRole")
    void givenValidLoginDto_whenLogin_thenReturnsTokenAndRole() throws Exception {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setSessionToken("login-token");
        user.setRole(com.bencawley.benspring.utilities.UserRole.USER);

        lenient().when(userService.login(any(UserLoginDTO.class))).thenReturn(user);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"));

        verify(sessionService).createSession(1L, "login-token");
    }

    @Test
    @DisplayName("givenInvalidLogin_whenLogin_thenReturnsUnauthorized")
    void givenInvalidLogin_whenLogin_thenReturnsUnauthorized() throws Exception {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("wrong");
        dto.setPassword("bad");

        lenient().when(userService.login(any(UserLoginDTO.class))).thenReturn(null);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @DisplayName("givenValidToken_whenLogout_thenReturnsSuccess")
    void givenValidToken_whenLogout_thenReturnsSuccess() throws Exception {
        // given
        String token = "valid";
        lenient().when(sessionService.validateSession(token)).thenReturn(1L);
        UserEntity user = new UserEntity();
        user.setId(1L);
        lenient().when(userService.getUserById(1L)).thenReturn(user);

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully."));

        verify(sessionService).invalidateSession(token);
    }

    @Test
    @DisplayName("givenInvalidToken_whenLogout_thenReturnsUnauthorized")
    void givenInvalidToken_whenLogout_thenReturnsUnauthorized() throws Exception {
        // given
        String token = "invalid";
        lenient().when(sessionService.validateSession(token)).thenReturn(null);

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid session token."));
    }

    @Test
    @DisplayName("givenNoToken_whenLogout_thenReturnsBadRequest")
    void givenNoToken_whenLogout_thenReturnsBadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest());
    }
}
