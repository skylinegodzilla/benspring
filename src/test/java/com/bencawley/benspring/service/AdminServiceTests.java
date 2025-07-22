package com.bencawley.benspring.service;

import com.bencawley.benspring.dtos.AdminUserDTO;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.services.AdminService;
import com.bencawley.benspring.services.UserService;
import com.bencawley.benspring.utilities.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTests {

    private UserService userService;
    private AdminService adminService;

    private UserEntity adminUser;
    private UserEntity normalUser;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        adminService = new AdminService(userService);

        adminUser = new UserEntity();
        adminUser.setUsername("admin");
        adminUser.setRole(UserRole.ADMIN);

        normalUser = new UserEntity();
        normalUser.setUsername("user");
        normalUser.setRole(UserRole.USER);
    }

    @Test
    void getAllUsers_asAdmin_returnsUsers() {
        // given
        when(userService.getAllUsers()).thenReturn(List.of(
                new AdminUserDTO("user1", "u1@example.com", UserRole.USER),
                new AdminUserDTO("user2", "u2@example.com", UserRole.ADMIN)
        ));

        // when
        List<AdminUserDTO> users = adminService.getAllUsers(adminUser);

        // then
        assertThat(users).hasSize(2);
        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_asNonAdmin_throws() {
        // when/then
        assertThatThrownBy(() -> adminService.getAllUsers(normalUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only admins");
    }

    @Test
    void getUserByUsername_asAdmin_returnsUserDTO() {
        // given
        when(userService.findByUsernameOrThrow("user")).thenReturn(normalUser);

        // when
        AdminUserDTO dto = adminService.getUserByUsername("user", adminUser);

        // then
        assertThat(dto.getUsername()).isEqualTo("user");
    }

    @Test
    void getUserByUsername_asNonAdmin_throws() {
        assertThatThrownBy(() -> adminService.getUserByUsername("user", normalUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only admins");
    }

    @Test
    void deleteUser_asAdmin_deletesUser() {
        // given
        when(userService.findByUsernameOrThrow("user")).thenReturn(normalUser);

        // when
        adminService.deleteUser("user", adminUser);

        // then
        verify(userService).delete(normalUser);
    }

    @Test
    void deleteUser_cannotDeleteSelf_throws() {
        assertThatThrownBy(() -> adminService.deleteUser("admin", adminUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("can't delete yourself");
    }

    @Test
    void changeUserRole_asAdmin_changesRole() {
        // given
        when(userService.findByUsernameOrThrow("user")).thenReturn(normalUser);

        // when
        adminService.changeUserRole("user", adminUser, UserRole.ADMIN);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void changeUserRole_cannotDemoteSelf_throws() {
        assertThatThrownBy(() ->
                adminService.changeUserRole("admin", adminUser, UserRole.USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("can't demote yourself");
    }

    @Test
    void resetUserPassword_asAdmin_resetsPassword() {
        // given
        when(userService.findByUsernameOrThrow("user")).thenReturn(normalUser);
        when(userService.encodePassword(anyString())).thenAnswer(i -> "hashed:" + i.getArgument(0));

        // when
        String newPassword = adminService.resetUserPassword("user", adminUser);

        // then
        assertThat(newPassword).isNotNull().hasSize(8);
        verify(userService).save(normalUser);
        assertThat(normalUser.getPasswordHash()).startsWith("hashed:");
    }

    @Test
    void resetUserPassword_asNonAdmin_throws() {
        assertThatThrownBy(() ->
                adminService.resetUserPassword("user", normalUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only admins");
    }
}
