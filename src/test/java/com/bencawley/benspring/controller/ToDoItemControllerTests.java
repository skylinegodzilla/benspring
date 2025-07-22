package com.bencawley.benspring.controller;

import com.bencawley.benspring.controllers.ToDoItemController;
import com.bencawley.benspring.dtos.ToDoItemRequestDTO;
import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.repositories.ToDoListRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ToDoItemControllerTests {

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();

    @Mock private SessionService sessionService;
    @Mock private ToDoListRepository listRepo;
    @Mock private ToDoItemRepository itemRepo;

    @BeforeEach
    void setUp() {
        var controller = new ToDoItemController(itemRepo, listRepo, sessionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("givenInvalidToken_whenGetAllItems_thenUnauthorized")
    void givenInvalidToken_whenGetAllItems_thenUnauthorized() throws Exception {
        // given
        long listId = 1L;
        lenient().when(sessionService.validateSession("bad-token")).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/todolists/{listId}/items", listId)
                        .header("Authorization", "bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("givenValidTokenButNoList_whenGetAllItems_thenNotFound")
    void givenValidTokenButNoList_whenGetAllItems_thenNotFound() throws Exception {
        // given
        long listId = 2L;
        lenient().when(sessionService.validateSession("token")).thenReturn(10L);
        when(listRepo.findById(listId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/todolists/{listId}/items", listId)
                        .header("Authorization", "token"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("givenValidCreateRequest_whenCreateItem_thenReturnsCreatedDto")
    void givenValidCreateRequest_whenCreateItem_thenReturnsCreatedDto() throws Exception {
        // given
        long listId = 4L;
        long userId = 30L;
        lenient().when(sessionService.validateSession("toke")).thenReturn(userId);

        var list = new ToDoListEntity(); list.setId(listId);
        var owner = new UserEntity(); owner.setId(userId);
        list.setUser(owner);
        when(listRepo.findById(listId)).thenReturn(Optional.of(list));
        when(itemRepo.findMaxPositionByListId(listId)).thenReturn(Optional.of(2));

        var req = new ToDoItemRequestDTO();
        req.setTitle("New Item");
        req.setDescription("Desc");
        req.setDueDate(LocalDate.of(2025, 11, 11));
        req.setCompleted(true);

        var saved = new ToDoItemEntity();
        saved.setId(99L);
        saved.setTitle(req.getTitle());
        saved.setDescription(req.getDescription());
        saved.setDueDate(req.getDueDate());
        saved.setCompleted(true);
        saved.setPosition(3);
        saved.setList(list);
        when(itemRepo.save(any(ToDoItemEntity.class))).thenReturn(saved);

        // when & then
        mockMvc.perform(post("/api/todolists/{listId}/items", listId)
                        .header("Authorization", "toke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Item"))
                .andExpect(jsonPath("$.position").value(3));
    }
}
