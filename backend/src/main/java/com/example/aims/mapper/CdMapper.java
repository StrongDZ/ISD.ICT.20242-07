package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.products.CdDTO;
import com.example.aims.model.CD;

@Mapper(componentModel = "spring")
public interface CdMapper {
    CdDTO toDTO(CD cd);

    CD toEntity(CdDTO cdDTO);
}

