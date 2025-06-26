package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.products.CdDTO;
import com.example.aims.model.CD;

@Mapper(componentModel = "spring")
public interface CdMapper extends ProductMapper<CD, CdDTO> {

    @Override
    CdDTO toDTO(CD cd);

    @Override
    CD toEntity(CdDTO cdDTO);

    @Override
    default String getProductType() {
        return "cd";
    }
}
