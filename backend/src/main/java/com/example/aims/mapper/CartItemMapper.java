package com.example.aims.mapper;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CartItemMapper {

    @Autowired
    protected ProductMapperHelper productMapperHelper;

    @Mapping(target = "productDTO", expression = "java(productMapperHelper.mapProductToDTO(cartItem.getProduct()))")
    public abstract CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", expression = "java(productMapperHelper.mapDTOToProduct(cartItemDTO.getProductDTO()))")
    public abstract CartItem toEntity(CartItemDTO cartItemDTO);

}
