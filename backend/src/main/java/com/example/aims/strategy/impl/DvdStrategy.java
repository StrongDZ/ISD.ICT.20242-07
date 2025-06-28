package com.example.aims.strategy.impl;

import com.example.aims.dto.products.DvdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.mapper.DvdMapper;
import com.example.aims.model.DVD;
import com.example.aims.repository.DVDRepository;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.common.ProductType;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DvdStrategy implements ProductStrategy {

    private final DVDRepository dvdRepository;
    private final DvdMapper dvdMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        DvdDTO dvdDTO = (DvdDTO) productDTO;

        // Create DVD entity (includes Product fields)
        DVD dvd = dvdMapper.toEntity(dvdDTO);
        dvd.setCategory(ProductType.dvd);
        DVD savedDvd = dvdRepository.save(dvd);

        // Return DTO
        return dvdMapper.toDTO(savedDvd);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        DvdDTO dvdDTO = (DvdDTO) productDTO;

        // Check if DVD exists
        if (!dvdRepository.existsById(id)) {
            throw new ResourceNotFoundException("DVD", "id", id);
        }

        // Update DVD
        DVD dvd = dvdMapper.toEntity(dvdDTO);
        dvd.setProductID(id);
        dvd.setCategory(ProductType.dvd);
        DVD savedDvd = dvdRepository.save(dvd);

        // Return DTO
        return dvdMapper.toDTO(savedDvd);
    }

    @Override
    public void deleteProduct(String id) {
        if (!dvdRepository.existsById(id)) {
            throw new ResourceNotFoundException("DVD", "id", id);
        }
        dvdRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id) {
        DVD dvd = dvdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DVD", "id", id));

        return dvdMapper.toDTO(dvd);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return dvdRepository.findAll().stream()
                .map(dvd -> (ProductDTO) dvdMapper.toDTO(dvd))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return dvdRepository.findAll().stream()
                .filter(dvd -> dvd.getTitle() != null &&
                        dvd.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .map(dvd -> (ProductDTO) dvdMapper.toDTO(dvd))
                .collect(Collectors.toList());
    }

    @Override
    public String getProductType() {
        return ProductType.dvd.name();
    }
}