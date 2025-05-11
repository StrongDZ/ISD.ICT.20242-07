package main.java.com.example.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryProductDTO {
    private String id;
    private int productPrice;
    private int productQuantity;
    private boolean supportRushOrder;
    private int weight;
    private int quantity;

    public DeliveryProductDTO() {
    }

    public DeliveryProductDTO(int productPrice, int productQuantity, boolean supportRushOrder, int weight) {
        this.productPrice = productPrice;
        this.quantity = productQuantity;
        this.supportRushOrder = supportRushOrder;
        this.weight = weight;
    }
}