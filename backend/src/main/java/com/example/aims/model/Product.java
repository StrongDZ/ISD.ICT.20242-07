package com.example.aims.model;

import jakarta.persistence.TemporalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Product")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {

    @Id
    private String productID;

    private String category;
    private String title;
    private Double value;
    private Double price;
    private Integer quantity;
    private String description;
    private String barcode;

    @Temporal(TemporalType.DATE)
    private Date warehouseEntryDate;

    private String dimensions;
    private Double weight;
    private String imageURL;
    private Boolean rushEligible;

}
