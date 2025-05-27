package com.bencawley.benspring.mappers;


import com.bencawley.benspring.dtos.ToDoListDTO;
import com.bencawley.benspring.entities.ToDoListEntity;

public class ToDoListMapper {

    public static ToDoListDTO toDto(ToDoListEntity entity) {
        ToDoListDTO dto = new ToDoListDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    public static ToDoListEntity toEntity(ToDoListDTO dto) {
        ToDoListEntity entity = new ToDoListEntity();
        entity.setId(dto.getId()); // null for new entries
        entity.setTitle(dto.getTitle());
        return entity;
    }
}