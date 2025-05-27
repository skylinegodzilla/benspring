package com.bencawley.benspring.mappers;

import com.bencawley.benspring.dtos.ToDoItemDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;

public class ToDoItemMapper {

    public static ToDoItemDTO toDTO(ToDoItemEntity entity) {
        ToDoItemDTO dto = new ToDoItemDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCompleted(entity.isCompleted());
        dto.setDueDate(entity.getDueDate());
        return dto;
    }

    public static ToDoItemEntity toEntity(ToDoItemDTO dto) {
        ToDoItemEntity entity = new ToDoItemEntity();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.isCompleted());
        entity.setDueDate(dto.getDueDate());
        return entity;
    }
}
