package com.example.aims.mapper;

import com.example.aims.dto.ProductDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final BookRepository bookRepository;
    private final CDRepository cdRepository;
    private final DVDRepository dvdRepository;

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductID(product.getProductID());
        dto.setCategory(product.getCategory());
        dto.setTitle(product.getTitle());
        dto.setValue(product.getValue());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setBarcode(product.getBarcode());
        dto.setWarehouseEntryDate(product.getWarehouseEntryDate());
        dto.setDimensions(product.getDimensions());
        dto.setWeight(product.getWeight());
        dto.setImageURL(product.getImageURL());
        
        if ("book".equalsIgnoreCase(product.getCategory())) {
            bookRepository.findById(product.getProductID()).ifPresent(book -> {
                dto.setCoverType(book.getCoverType());
                dto.setAuthors(book.getAuthors());
                dto.setPublisher(book.getPublisher());
                dto.setNumberOfPages(book.getNumberOfPages());
                dto.setLanguage(book.getLanguage());
                dto.setGenre(book.getGenre());
                dto.setPubDate(book.getPubDate());
            });
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            cdRepository.findById(product.getProductID()).ifPresent(cd -> {
                dto.setTracklist(cd.getTracklist());
                dto.setArtist(cd.getArtist());
                dto.setReleaseDate(cd.getReleaseDate());
                dto.setRecordLabel(cd.getRecordLabel());
                dto.setMusicType(cd.getMusicType());
            });
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            dvdRepository.findById(product.getProductID()).ifPresent(dvd -> {
                dto.setDiscType(dvd.getDiscType());
                dto.setRuntime(dvd.getRuntime());
                dto.setStudio(dvd.getStudio());
                dto.setDirector(dvd.getDirector());
                dto.setSubtitle(dvd.getSubtitle());
                dto.setReleaseDate(dvd.getReleaseDate());
                dto.setLanguage(dvd.getLanguage());
                dto.setGenre(dvd.getGenre());
            });
        }
        
        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setProductID(dto.getProductID());
        product.setCategory(dto.getCategory());
        product.setTitle(dto.getTitle());
        product.setValue(dto.getValue());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setDescription(dto.getDescription());
        product.setBarcode(dto.getBarcode());
        product.setWarehouseEntryDate(dto.getWarehouseEntryDate());
        product.setDimensions(dto.getDimensions());
        product.setWeight(dto.getWeight());
        product.setImageURL(dto.getImageURL());
        return product;
    }
} 