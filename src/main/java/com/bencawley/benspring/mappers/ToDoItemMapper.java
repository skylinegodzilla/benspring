package com.bencawley.benspring.mappers;
import com.bencawley.benspring.dtos.ToDoItemRequestDTO;
import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;

/*
    The job of a mapper... well these mappers
    is to map an entity (An Object that is stored in the DB) to a DTO (An Object that will be converted to JSON to be sent through an API for requests and responses)
    And vice verser convert a DTO to an entity.
    This is because we don't want the endpoints to have direct access to the DB and the data stored in the DB is not the same as the Data sent in API calls

     We have request DTO -> entity for incoming data
     and entity -> response DTO for outgoing data.
 */

public class ToDoItemMapper {

    // Map entity -> response DTO (outgoing)
    public static ToDoItemResponseDTO toResponseDTO(ToDoItemEntity entity) {
        ToDoItemResponseDTO dto = new ToDoItemResponseDTO();
        dto.setItemId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCompleted(entity.isCompleted());
        dto.setDueDate(entity.getDueDate());
        dto.setPosition(entity.getPosition());
        return dto;
    }

    // Map request DTO -> entity (incoming)
    public static ToDoItemEntity toEntity(ToDoItemRequestDTO dto, ToDoListEntity list) {
        ToDoItemEntity entity = new ToDoItemEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCompleted(dto.isCompleted());
        entity.setDueDate(dto.getDueDate());
        entity.setPosition(dto.getPosition());
        entity.setList(list);
        return entity;
    }
}

