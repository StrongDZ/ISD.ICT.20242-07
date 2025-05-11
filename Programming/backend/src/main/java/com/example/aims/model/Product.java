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
@Table(name = "Product")
public class Product {
    // Cohesion: High – all fields/methods relate to product representation.
    // SRP: Mild violation – includes both data and rush eligibility logic.
    // Suggestion: move isRushEligible() to a separate RushEligibilityPolicy if logic grows.

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
    private boolean rushEligible;
    public boolean isRushEligible() {
        return rushEligible;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRushEligible(boolean rushEligible) {
        this.rushEligible = rushEligible;
    }
}