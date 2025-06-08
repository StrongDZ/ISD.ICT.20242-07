package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Book")
@PrimaryKeyJoinColumn(name = "productID")
public class Book extends Product {

    private String coverType;
    private String authors;
    private String publisher;
    private Integer numberOfPages;
    private String language;
    private String genre;

    @Temporal(TemporalType.DATE)
    private Date pubDate;
}