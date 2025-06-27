package com.example.aims.dto;

/**
 * DTO (Data Transfer Object) để chứa các loại phí vận chuyển được tính toán.
 * Đối tượng này sẽ được trả về dưới dạng JSON trong API response.
 */
public class DeliveryFeeResponseDTO {

    private double regularShippingFee;
    private double rushShippingFee;

    // Constructors
    public DeliveryFeeResponseDTO() {
    }

    public DeliveryFeeResponseDTO(double regularShippingFee, double rushShippingFee) {
        this.regularShippingFee = regularShippingFee;
        this.rushShippingFee = rushShippingFee;
    }

    // Getters and Setters
    public double getRegularShippingFee() {
        return regularShippingFee;
    }

    public void setRegularShippingFee(double regularShippingFee) {
        this.regularShippingFee = regularShippingFee;
    }

    public double getRushShippingFee() {
        return rushShippingFee;
    }

    public void setRushShippingFee(double rushShippingFee) {
        this.rushShippingFee = rushShippingFee;
    }
}
