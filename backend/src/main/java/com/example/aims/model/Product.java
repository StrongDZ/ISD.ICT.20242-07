package com.example.aims.model;

import jakarta.persistence.TemporalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import com.example.aims.common.ProductType;
import com.example.aims.exception.BadRequestException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Product")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productID;

    @Enumerated(EnumType.STRING)
    private ProductType category;
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
    private Boolean eligible;
    
    // Price update tracking fields
    private Double oldPrice;
    private Integer updateCount = 0;
    
    private LocalDate updateAt = LocalDate.now();

    /**
     * Validates that there is sufficient stock for the requested quantity.
     *
     * @param requestedQuantity the quantity to validate against current stock
     * @throws com.example.aims.exception.BadRequestException if insufficient stock
     */
    public void validateStock(Integer requestedQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new com.example.aims.exception.BadRequestException("Quantity must be greater than zero");
        }

        if (this.quantity < requestedQuantity) {
            throw new com.example.aims.exception.BadRequestException(
                    "Not enough stock available. Available: " + this.quantity);
        }
    }

    public void setQuantity(Integer quantity) { 
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

}
