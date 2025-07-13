package com.bencawley.benspring.mappers;

import com.bencawley.benspring.dtos.MeDTO;
import com.bencawley.benspring.entities.UserEntity;

public class MeMapper {
    public static MeDTO toResponseDTO(UserEntity user) {
        MeDTO dto = new MeDTO();
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
