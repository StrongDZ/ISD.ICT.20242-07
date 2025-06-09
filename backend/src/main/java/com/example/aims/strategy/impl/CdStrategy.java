package com.example.aims.strategy.impl;

import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.mapper.CdMapper;
import com.example.aims.model.CD;
import com.example.aims.repository.CDRepository;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.util.IdGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CdStrategy implements ProductStrategy {

    private final CDRepository cdRepository;
    private final CdMapper cdMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        CdDTO cdDTO = (CdDTO) productDTO;

        // Create CD entity (includes Product fields)
        CD cd = cdMapper.toEntity(cdDTO);
        if (cd.getProductID() == null) {
            cd.setProductID(IdGenerator.generateProductId("cd"));
        }
        cd.setCategory("cd");
        CD savedCd = cdRepository.save(cd);

        // Return DTO
        return cdMapper.toDTO(savedCd);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        CdDTO cdDTO = (CdDTO) productDTO;

        // Check if CD exists
        if (!cdRepository.existsById(id)) {
            throw new ResourceNotFoundException("CD", "id", id);
        }

        // Update CD
        CD cd = cdMapper.toEntity(cdDTO);
        cd.setProductID(id);
        cd.setCategory("cd");
        CD savedCd = cdRepository.save(cd);

        // Return DTO
        return cdMapper.toDTO(savedCd);
    }

    @Override
    public void deleteProduct(String id) {
        if (!cdRepository.existsById(id)) {
            throw new ResourceNotFoundException("CD", "id", id);
        }
        cdRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id) {
        CD cd = cdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CD", "id", id));

        return cdMapper.toDTO(cd);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return cdRepository.findAll().stream()
                .map(cd -> (ProductDTO) cdMapper.toDTO(cd))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return cdRepository.findAll().stream()
                .filter(cd -> cd.getTitle() != null &&
                        cd.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .map(cd -> (ProductDTO) cdMapper.toDTO(cd))
                .collect(Collectors.toList());
    }

    @Override
    public String getProductType() {
        return "cd";
    }
}