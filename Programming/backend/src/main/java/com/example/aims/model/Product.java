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

/**
 * ✅ Represents a product in the system.
 * ✅ High Cohesion: All fields and methods relate to a product's identity and rush eligibility.
 * ✅ SRP Compliant: Only stores product-related data — no unrelated logic.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): This class has a single responsibility:
 *   to represent product data including rush eligibility.
 * - O (Open/Closed Principle):
 *   The class can be extended with new fields or methods if necessary without modifying existing behavior.
 * - L (Liskov Substitution Principle):
 *   As a simple POJO, it can be safely substituted wherever a Product object is expected.
 * - I (Interface Segregation Principle):
 *   Not applicable here since this is a concrete data class, no interfaces implemented.
 * - D (Dependency Inversion Principle):
 *   This class does not depend on any lower-level modules and is a low-level data holder.
 */
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String productID;             
    private String title;
     private Double value;
    private Double price;
    private Integer quantity;
    private String description;
    private String barcode;
    private String category;
    @Temporal(TemporalType.DATE)
    private Date warehouseEntryDate;
    
    private String dimensions;
    private Double weight;
    private String imageURL;              
    private boolean isRushEligible; 


    // ✅ Constructor with all fields — convenient for manual creation
    public Product(String productID, String title, boolean isRushEligible) {
        this.productID = productID;
        this.title = title;
        this.isRushEligible = isRushEligible;
    }

    // ✅ Getter for rush eligibility
    public boolean isRushEligible() {
        return isRushEligible;
    }

    // ✅ Setter for rush eligibility
    public void setRushEligible(boolean rushEligible) {
        isRushEligible = rushEligible;
    }

    // ✅ Getter for ID
    public String getId() {
        return productID;
    }

    // ✅ Getter for name
    public String getName() {
        return title;
    }

    // ✅ Setter for ID
    public void setId(String id) {
        this.productID = id;
    }

    // ✅ Setter for name
    public void setName(String title) {
        this.title = title;
    }
}
