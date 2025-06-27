package com.example.aims.mapper;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductMapperFactory;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CartItemMapper {

    @Autowired
    protected ProductMapperFactory mapperFactory;

    @Mapping(source = "product", target = "productDTO", qualifiedByName = "mapProductToDTO")
    public abstract CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    public abstract CartItem toEntity(CartItemDTO cartItemDTO);

    @Named("mapProductToDTO")
    @SuppressWarnings("unchecked")
    protected ProductDTO mapProductToDTO(Product product) {
        if (product == null || product.getCategory() == null) {
            return null;
        }

        ProductMapper<?, ?> mapper = mapperFactory.getMapper(product.getCategory());
        return ((ProductMapper<Product, ProductDTO>) mapper).toDTO(product);
    }

    @Named("mapDTOToProduct")
    @SuppressWarnings("unchecked")
    protected Product mapDTOToProduct(ProductDTO productDTO) {
        if (productDTO == null || productDTO.getCategory() == null) {
            return null;
        }

        ProductMapper<?, ?> mapper = mapperFactory.getMapper(productDTO.getCategory());
        return ((ProductMapper<Product, ProductDTO>) mapper).toEntity(productDTO);
    }

}
