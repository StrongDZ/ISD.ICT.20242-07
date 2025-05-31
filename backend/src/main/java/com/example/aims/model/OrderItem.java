package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrderItems")
public class OrderItem {
    
    @EmbeddedId
    private OrderItemId id;
    
    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "productID")
    private Product product;
    
    @ManyToOne
    @MapsId("orderID")
    @JoinColumn(name = "orderID")
    private Order order;
    
    private Integer quantity;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemId implements java.io.Serializable {
        private String productID;
        private Integer orderID;
    }
}