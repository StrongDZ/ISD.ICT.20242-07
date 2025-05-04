package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productID;
    private String category;
    private String title;
    private Double value;
    private Double price;
    private Integer quantity;
    private String description;
    private String barcode;
    private Date warehouseEntryDate;
    private String dimensions;
    private Double weight;
    private String imageURL;
    
    // Additional fields for specific product types
    // Book fields
    private String coverType;
    private String authors;
    private String publisher;
    private Integer numberOfPages;
    private String language;
    private String genre;
    private Date pubDate;
    
    // CD fields
    private String tracklist;
    private String artist;
    private Date releaseDate;
    private String recordLabel;
    private String musicType;
    
    // DVD fields
    private String discType;
    private String runtime;
    private String studio;
    private String director;
    private String subtitle;
    // releaseDate and language are already defined above
    // genre is already defined above
}