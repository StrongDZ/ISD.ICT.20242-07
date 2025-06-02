package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ShopItems")
public class ShopItem {
    
    @EmbeddedId
    private ShopItemId id;
    
    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "productID")
    private Product product;
    
    @ManyToOne
    @MapsId("managerID")
    @JoinColumn(name = "managerID")
    private Users manager;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopItemId implements java.io.Serializable {
        private String productID;
        private String managerID;
    }
}