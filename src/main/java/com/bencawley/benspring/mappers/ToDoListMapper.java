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

        // TODO: This is a part I still dont fully understand I just know it works :P
        List<ToDoItemResponseDTO> itemDTOs = entity.getItems()
                .stream()
                .map(ToDoItemMapper::toResponseDTO)
                .toList();

        dto.setItems(itemDTOs);
        return dto;
    }

    /*
        This is for creating a list so we only need the title and description.
        The database will automatically create the ID
        and the items hold a reference to what list they belong to
        TODO: I still dont know how it creates the user_id reference I know it has it i just cant remember how it makes it
        ohh so in the controller we say entity.setUser(user); after we call this mapper to set the user ID
    */
    public static ToDoListEntity toEntity(ToDoListRequestDTO dto) {
        ToDoListEntity entity = new ToDoListEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}