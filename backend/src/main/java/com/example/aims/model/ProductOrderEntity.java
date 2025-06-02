package com.example.aims.model;

import com.example.aims.id.ProductOrderId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ProductOrderId.class)
public class ProductOrderEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "orderID")
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    private Integer quantity;
}