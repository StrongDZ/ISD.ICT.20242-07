package main.java.com.example.aims.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
// ***Cohesion: high
// This entity class has high cohesion: it directly models the relationship between an order and a product, including the quantity ordered.
// All attributes are tightly related to a single concept – a product in an order.

// ***SRP Violation: no
// This class adheres to the Single Responsibility Principle. It only represents the data structure for the product-order relationship,
// and does not include business logic or unrelated data operations.

// ***Design Note:
// The class plays the role of a join table (many-to-one with both product and order), which is typical in a many-to-many relationship with extra fields (like quantity).
// It’s a well-structured part of the domain model and doesn’t require decomposition.

public class ProductOrderEntity {
    @Column(name = "quantity")
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}