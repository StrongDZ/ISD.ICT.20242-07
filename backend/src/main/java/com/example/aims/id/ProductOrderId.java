package com.example.aims.id;
import java.io.Serializable;
import java.util.Objects;

public class ProductOrderId implements Serializable {
    private String order;
    private Long product;

    public ProductOrderId() {}

    public ProductOrderId(String order, Long product) {
        this.order = order;
        this.product = product;
    }

    // equals() & hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductOrderId)) return false;
        ProductOrderId that = (ProductOrderId) o;
        return Objects.equals(order, that.order) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}
