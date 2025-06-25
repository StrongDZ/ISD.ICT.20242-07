package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CartItems")
public class CartItem {
    @EmbeddedId
    private CartItemId id;

    @ManyToOne
    @MapsId("userID")
    @JoinColumn(name = "userID", insertable = false, updatable = false)
    private Users customer;

    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "productID")
    private Product product;

    private Integer quantity;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemId implements java.io.Serializable {
        private Integer userID;
        private String productID;
    }
}