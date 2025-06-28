package com.example.aims.dto.products;

import java.util.Date;

import com.example.aims.validator.OnResponse;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookDTO.class, name = "book"),
        @JsonSubTypes.Type(value = CdDTO.class, name = "cd"),
        @JsonSubTypes.Type(value = DvdDTO.class, name = "dvd")
})
public abstract class ProductDTO {
    @NotNull(groups = OnResponse.class, message = "Phải có ID khi trả về")
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
    private Boolean eligible;

    // Manual getter and setter for eligible to ensure compatibility
    public Boolean getEligible() {
        return eligible;
    }

    public void setEligible(Boolean eligible) {
        this.eligible = eligible;
    }

    // Manual getter for productID to ensure compatibility
    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
