package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Book")
public class Book {
    
    @Id
    private String productID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "productID")
    private Product product;
    
    private String coverType;
    private String authors;
    private String publisher;
    private Integer numberOfPages;
    private String language;
    private String genre;
    
    @Temporal(TemporalType.DATE)
    private Date pubDate;
}