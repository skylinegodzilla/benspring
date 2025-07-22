package com.bencawley.benspring.controller;

import com.bencawley.benspring.controllers.ToDoListController;
import com.bencawley.benspring.dtos.ToDoListRequestDTO;
import com.bencawley.benspring.dtos.ToDoListResponseDTO;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.ToDoListRepository;
import com.bencawley.benspring.repositories.UserRepository;
import com.bencawley.benspring.services.SessionService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ToDoListControllerTests {

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();

    @Mock private SessionService sessionService;
    @Mock private ToDoListRepository listRepo;
    @Mock private UserRepository userRepo;

    @BeforeEach
    void setup() {
        // Given a standalone ToDoListController
        var controller = new ToDoListController(listRepo, sessionService, userRepo);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("givenInvalidToken_whenGetAllLists_thenUnauthorized")
    void givenInvalidToken_whenGetAllLists_thenUnauthorized() throws Exception {
        // Given
        lenient().when(sessionService.validateSession("bad")).thenReturn(null);
        // When & Then
        mockMvc.perform(get("/api/todolists").header("Authorization","bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("givenValidToken_whenGetAllLists_thenReturnsList")
    void givenValidToken_whenGetAllLists_thenReturnsList() throws Exception {
        // Given
        long userId = 7L;
        lenient().when(sessionService.validateSession("tok")).thenReturn(userId);
        var l1 = new ToDoListEntity(); l1.setId(1L); l1.setTitle("L1");
        var l2 = new ToDoListEntity(); l2.setId(2L); l2.setTitle("L2");
        when(listRepo.findByUser_Id(userId)).thenReturn(List.of(l1, l2));
        // When & Then
        mockMvc.perform(get("/api/todolists").header("Authorization","tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("L1"));
    }

    @Test
    @DisplayName("givenUnauthorizedUser_whenCreateList_thenUnauthorized")
    void givenUnauthorizedUser_whenCreateList_thenUnauthorized() throws Exception {
        // Given
        lenient().when(sessionService.validateSession("tok")).thenReturn(5L);
        when(userRepo.findById(5L)).thenReturn(Optional.empty());
        var req = new ToDoListRequestDTO(); req.setTitle("New");
        // When & Then
        mockMvc.perform(post("/api/todolists")
                        .header("Authorization","tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("givenValidUser_whenCreateList_thenReturnsDto")
    void givenValidUser_whenCreateList_thenReturnsDto() throws Exception {
        // Given
        var user = new UserEntity(); user.setId(9L);
        lenient().when(sessionService.validateSession("tok")).thenReturn(9L);
        when(userRepo.findById(9L)).thenReturn(Optional.of(user));
        var req = new ToDoListRequestDTO(); req.setTitle("ListX");
        var saved = new ToDoListEntity(); saved.setId(99L); saved.setTitle("ListX"); saved.setUser(user);
        when(listRepo.save(any(ToDoListEntity.class))).thenReturn(saved);
        // When & Then
        mockMvc.perform(post("/api/todolists")
                        .header("Authorization","tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listId").value(99))
                .andExpect(jsonPath("$.title").value("ListX"));
    }

    @Test
    @DisplayName("givenMissingList_whenUpdateList_thenNotFound")
    void givenMissingList_whenUpdateList_thenNotFound() throws Exception {
        // Given
        lenient().when(sessionService.validateSession("tok")).thenReturn(1L);
        var req = new ToDoListRequestDTO(); req.setListId(55L); req.setTitle("X");
        when(listRepo.findById(55L)).thenReturn(Optional.empty());
        // When & Then
        mockMvc.perform(put("/api/todolists")
                        .header("Authorization","tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("givenWrongOwner_whenUpdateList_thenUnauthorized")
    void givenWrongOwner_whenUpdateList_thenUnauthorized() throws Exception {
        // Given
        lenient().when(sessionService.validateSession("tok")).thenReturn(2L);
        var entity = new ToDoListEntity(); entity.setId(3L); var u = new UserEntity(); u.setId(999L); entity.setUser(u);
        when(listRepo.findById(3L)).thenReturn(Optional.of(entity));
        var req = new ToDoListRequestDTO(); req.setListId(3L); req.setTitle("X");
        // When & Then
        mockMvc.perform(put("/api/todolists")
                        .header("Authorization","tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("givenValidOwner_whenUpdateList_thenReturnsUpdatedDto")
    void givenValidOwner_whenUpdateList_thenReturnsUpdatedDto() throws Exception {
        // Given
        long userId = 5L;
        lenient().when(sessionService.validateSession("tok")).thenReturn(userId);
        var owner = new UserEntity(); owner.setId(userId);
        var entity = new ToDoListEntity(); entity.setId(4L); entity.setUser(owner); entity.setTitle("Old");
        when(listRepo.findById(4L)).thenReturn(Optional.of(entity));
        var req = new ToDoListRequestDTO(); req.setListId(4L); req.setTitle("New");
        var updated = new ToDoListEntity(); updated.setId(4L); updated.setUser(owner); updated.setTitle("New");
        when(listRepo.save(entity)).thenReturn(updated);
        // When & Then
        mockMvc.perform(put("/api/todolists")
                        .header("Authorization","tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"));
    }

    @Test
    @DisplayName("givenMissingList_whenDeleteList_thenNotFound")
    void givenMissingList_whenDeleteList_thenNotFound() throws Exception {
        // Given
        lenient().when(sessionService.validateSession("tok")).thenReturn(8L);
        when(listRepo.findById(42L)).thenReturn(Optional.empty());
        // When & Then
        mockMvc.perform(delete("/api/todolists/{id}",42L)
                        .header("Authorization","tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("givenWrongOwner_whenDeleteList_thenUnauthorized")
    void givenWrongOwner_whenDeleteList_thenUnauthorized() throws Exception {
        // Given
        long id = 7L;
        lenient().when(sessionService.validateSession("tok")).thenReturn(9L);
        var entity = new ToDoListEntity(); entity.setId(id); var u = new UserEntity(); u.setId(999L); entity.setUser(u);
        when(listRepo.findById(id)).thenReturn(Optional.of(entity));
        // When & Then
        mockMvc.perform(delete("/api/todolists/{id}",id)
                        .header("Authorization","tok"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("givenValidOwner_whenDeleteList_thenReturnsRemainingLists")
    void givenValidOwner_whenDeleteList_thenReturnsRemainingLists() throws Exception {
        // Given
        long id = 3L, userId = 15L;
        lenient().when(sessionService.validateSession("tok")).thenReturn(userId);
        var owner = new UserEntity(); owner.setId(userId);
        var entity = new ToDoListEntity(); entity.setId(id); entity.setUser(owner);
        when(listRepo.findById(id)).thenReturn(Optional.of(entity));
        var rem = new ToDoListEntity(); rem.setId(8L); rem.setUser(owner);
        when(listRepo.findByUser_Id(userId)).thenReturn(List.of(rem));
        // When & Then
        mockMvc.perform(delete("/api/todolists/{id}",id)
                        .header("Authorization","tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].listId").value(8));
    }
}
