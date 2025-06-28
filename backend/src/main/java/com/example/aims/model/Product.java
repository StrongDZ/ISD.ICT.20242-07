package com.example.aims.model;

import jakarta.persistence.TemporalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.example.aims.common.ProductType;

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

}
