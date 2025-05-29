package com.bencawley.benspring.mappers;
import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.dtos.ToDoListRequestDTO;
import com.bencawley.benspring.dtos.ToDoListResponseDTO;
import com.bencawley.benspring.entities.ToDoListEntity;

import java.util.List;

public class ToDoListMapper {

    public static ToDoListResponseDTO toResponseDTO(ToDoListEntity entity) {
        ToDoListResponseDTO dto = new ToDoListResponseDTO();
        dto.setListId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());

        List<ToDoItemResponseDTO> itemDTOs = entity.getItems()
                .stream()
                .map(ToDoItemMapper::toResponseDTO)
                .toList();

        dto.setItems(itemDTOs);
        return dto;
    }

    public static ToDoListEntity toEntity(ToDoListRequestDTO dto) {
        ToDoListEntity entity = new ToDoListEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}