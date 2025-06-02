package com.example.aims.dto;

import java.util.List;

import com.example.aims.model.DeliveryInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceDTO {
    private List<DeliveryProductDTO> cart;
    private DeliveryInfo deliveryInfo;
    private int subtotal;
    private int deliveryFee;
    private int vat;
}
