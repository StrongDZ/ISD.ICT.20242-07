package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.common.ProductType;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.model.DVD;

@Mapper(componentModel = "spring")
public interface DvdMapper extends ProductMapper<DVD, DvdDTO> {

    @Override
    DvdDTO toDTO(DVD dvd);

    @Override
    DVD toEntity(DvdDTO dvdDTO);

    @Override
    default ProductType getProductType() {
        return ProductType.dvd;
    }
}
