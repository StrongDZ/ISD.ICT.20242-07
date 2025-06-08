package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.products.DvdDTO;
import com.example.aims.model.DVD;

@Mapper(componentModel = "spring")
public interface DvdMapper {
    DvdDTO toDTO(DVD dvd);

    DVD toEntity(DvdDTO dvdDTO);
}
