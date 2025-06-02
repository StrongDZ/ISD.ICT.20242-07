package com.example.aims.factory;

import com.example.aims.dto.ProductDTO;
import com.example.aims.model.*;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {
    public Product createProduct(ProductDTO dto) {
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

    public Book createBook(ProductDTO dto, Product product) {
        Book book = new Book();
        book.setProductID(product.getProductID());
        book.setProduct(product);
        book.setCoverType(dto.getCoverType());
        book.setAuthors(dto.getAuthors());
        book.setPublisher(dto.getPublisher());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setLanguage(dto.getLanguage());
        book.setGenre(dto.getGenre());
        book.setPubDate(dto.getPubDate());
        return book;
    }

    public CD createCD(ProductDTO dto, Product product) {
        CD cd = new CD();
        cd.setProductID(product.getProductID());
        cd.setProduct(product);
        cd.setTracklist(dto.getTracklist());
        cd.setArtist(dto.getArtist());
        cd.setReleaseDate(dto.getReleaseDate());
        cd.setRecordLabel(dto.getRecordLabel());
        cd.setMusicType(dto.getMusicType());
        return cd;
    }

    public DVD createDVD(ProductDTO dto, Product product) {
        DVD dvd = new DVD();
        dvd.setProductID(product.getProductID());
        dvd.setProduct(product);
        dvd.setDiscType(dto.getDiscType());
        dvd.setRuntime(dto.getRuntime());
        dvd.setStudio(dto.getStudio());
        dvd.setDirector(dto.getDirector());
        dvd.setSubtitle(dto.getSubtitle());
        dvd.setReleaseDate(dto.getReleaseDate());
        dvd.setLanguage(dto.getLanguage());
        dvd.setGenre(dto.getGenre());
        return dvd;
    }
} 