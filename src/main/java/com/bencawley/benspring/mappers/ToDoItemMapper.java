package com.bencawley.benspring.mappers;
import com.bencawley.benspring.dtos.ToDoItemRequestDTO;
import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;

public class ToDoItemMapper {

    // Map entity -> response DTO (outgoing)
    public static ToDoItemResponseDTO toResponseDTO(ToDoItemEntity entity) {
        ToDoItemResponseDTO dto = new ToDoItemResponseDTO();
        dto.setItemId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCompleted(entity.isCompleted());
        dto.setDueDate(entity.getDueDate());
        return dto;
    }

    // Map request DTO -> entity (incoming)
    public static ToDoItemEntity toEntity(ToDoItemRequestDTO dto, ToDoListEntity list) {
        ToDoItemEntity entity = new ToDoItemEntity();
        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.isCompleted());
        entity.setDueDate(dto.getDueDate());
        entity.setList(list);
        return entity;
    }
}

