package com.example.aims.dto.products;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import com.example.aims.validator.OnRequest;
import com.example.aims.validator.OnResponse;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookDTO.class, name = "book"),
        @JsonSubTypes.Type(value = CdDTO.class, name = "cd"),
        @JsonSubTypes.Type(value = DvdDTO.class, name = "dvd")
})
public abstract class ProductDTO {
    @Null(groups = OnRequest.class, message = "Không cần truyền ID khi gửi request")
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
    private Boolean isRushEligible;
}
