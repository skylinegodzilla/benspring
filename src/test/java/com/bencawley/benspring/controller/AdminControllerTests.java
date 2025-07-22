package com.bencawley.benspring.controller;

import com.bencawley.benspring.controllers.AdminController;
import com.bencawley.benspring.dtos.AdminUserDTO;
import com.bencawley.benspring.dtos.ChangeUserRoleRequestDTO;
import com.bencawley.benspring.dtos.MessageDTO;
import com.bencawley.benspring.dtos.PasswordResetResponseDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.AdminService;
import com.bencawley.benspring.services.SessionService;
import com.bencawley.benspring.services.UserService;
import com.bencawley.benspring.utilities.UserRole;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTests {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;
    @Mock private AdminService adminService;
    @Mock private SessionService sessionService;

    @BeforeEach
    void setup() {
        AdminController controller = new AdminController(userService, adminService, sessionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("givenNonAdminToken_whenGetAllUsers_thenThrowsException")
    void givenNonAdminToken_whenGetAllUsers_thenThrowsException() throws Exception {
        // given
        String token = "token";
        UserEntity user = new UserEntity(); user.setRole(UserRole.USER);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(user);
        when(adminService.getAllUsers(user)).thenThrow(new RuntimeException("Only admins can perform this action."));

        // when & then: exception is thrown due to unhandled RuntimeException
        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/admin/users").header("Authorization", token))
        );
    }

    @Test
    @DisplayName("givenValidAdmin_whenDeleteUser_thenReturnsSuccess")
    void givenValidAdmin_whenDeleteUser_thenReturnsSuccess() throws Exception {
        // given
        String token = "t";
        UserEntity admin = new UserEntity(); admin.setRole(UserRole.ADMIN);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(admin);
        // when & then
        mockMvc.perform(delete("/api/admin/remove/targetUser").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
        verify(adminService).deleteUser("targetUser", admin);
    }

    @Test
    @DisplayName("givenInvalidRole_whenChangeUserRole_thenBadRequest")
    void givenInvalidRole_whenChangeUserRole_thenBadRequest() throws Exception {
        // given
        String token = "t";
        UserEntity admin = new UserEntity(); admin.setRole(UserRole.ADMIN);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(admin);
        ChangeUserRoleRequestDTO req = new ChangeUserRoleRequestDTO(); req.setNewRole("invalid");

        // when & then
        mockMvc.perform(patch("/api/admin/users/u/role").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid role."));
    }

    @Test
    @DisplayName("givenValidRoleChange_whenChangeUserRole_thenReturnsSuccess")
    void givenValidRoleChange_whenChangeUserRole_thenReturnsSuccess() throws Exception {
        // given
        String token = "t";
        UserEntity admin = new UserEntity(); admin.setRole(UserRole.ADMIN);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(admin);
        ChangeUserRoleRequestDTO req = new ChangeUserRoleRequestDTO(); req.setNewRole("USER");

        // when & then
        mockMvc.perform(patch("/api/admin/users/u/role").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User role updated to USER"));

        verify(adminService).changeUserRole("u", admin, UserRole.USER);
    }

    @Test
    @DisplayName("givenValidUsername_whenGetUserByUsername_thenReturnsUserDto")
    void givenValidUsername_whenGetUserByUsername_thenReturnsUserDto() throws Exception {
        // given
        String token = "t";
        UserEntity admin = new UserEntity(); admin.setRole(UserRole.ADMIN);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(admin);
        AdminUserDTO dto = new AdminUserDTO("u","e",UserRole.USER);
        when(adminService.getUserByUsername("u", admin)).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/admin/users/u").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("u"));
    }

    @Test
    @DisplayName("givenValidUsername_whenResetPassword_thenReturnsPassword")
    void givenValidUsername_whenResetPassword_thenReturnsPassword() throws Exception {
        // given
        String token = "t";
        UserEntity admin = new UserEntity(); admin.setRole(UserRole.ADMIN);
        lenient().when(userService.findBySessionTokenOrThrow(token)).thenReturn(admin);
        when(adminService.resetUserPassword("u", admin)).thenReturn("newpass");

        // when & then
        mockMvc.perform(post("/api/admin/users/u/reset-password").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newPassword").value("newpass"));
    }
}
