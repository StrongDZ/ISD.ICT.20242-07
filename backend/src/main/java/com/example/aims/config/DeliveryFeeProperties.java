package com.example.aims.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ✅ Configuration properties cho phí giao hàng
 * ✅ High Cohesion: Tập trung vào cấu hình phí giao hàng
 * ✅ Single Responsibility Principle: Chỉ xử lý binding config properties
 * ✅ Open/Closed Principle: Có thể mở rộng thêm properties mà không thay đổi code hiện tại
 * ✅ Dependency Inversion Principle: Externalize configuration, để Spring inject
 */
@Component
@ConfigurationProperties(prefix = "delivery.fee")
public class DeliveryFeeProperties {

    private Regular regular = new Regular();
    private InnerCity innerCity = new InnerCity();
    private FreeShipping freeShipping = new FreeShipping();
    private RushOrder rushOrder = new RushOrder();

    public static class Regular {
        private double initialFee = 30000.0;
        private double extraFeePerUnit = 2500.0;
        private double weightUnit = 0.5;

        public double getInitialFee() { return initialFee; }
        public void setInitialFee(double initialFee) { this.initialFee = initialFee; }
        
        public double getExtraFeePerUnit() { return extraFeePerUnit; }
        public void setExtraFeePerUnit(double extraFeePerUnit) { this.extraFeePerUnit = extraFeePerUnit; }
        
        public double getWeightUnit() { return weightUnit; }
        public void setWeightUnit(double weightUnit) { this.weightUnit = weightUnit; }
    }

    public static class InnerCity {
        private double initialFee = 22000.0;
        private double weightUnit = 3.0;
        private double extraFeePerUnit = 2500.0;

        public double getInitialFee() { return initialFee; }
        public void setInitialFee(double initialFee) { this.initialFee = initialFee; }
        
        public double getWeightUnit() { return weightUnit; }
        public void setWeightUnit(double weightUnit) { this.weightUnit = weightUnit; }
        
        public double getExtraFeePerUnit() { return extraFeePerUnit; }
        public void setExtraFeePerUnit(double extraFeePerUnit) { this.extraFeePerUnit = extraFeePerUnit; }
    }

    public static class FreeShipping {
        private double threshold = 100000.0;
        private double maxAmount = 25000.0;

        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
        
        public double getMaxAmount() { return maxAmount; }
        public void setMaxAmount(double maxAmount) { this.maxAmount = maxAmount; }
    }

    public static class RushOrder {
        private double feePerItem = 10000.0;

        public double getFeePerItem() { return feePerItem; }
        public void setFeePerItem(double feePerItem) { this.feePerItem = feePerItem; }
    }

    // Getters và Setters
    public Regular getRegular() { return regular; }
    public void setRegular(Regular regular) { this.regular = regular; }

    public InnerCity getInnerCity() { return innerCity; }
    public void setInnerCity(InnerCity innerCity) { this.innerCity = innerCity; }

    public FreeShipping getFreeShipping() { return freeShipping; }
    public void setFreeShipping(FreeShipping freeShipping) { this.freeShipping = freeShipping; }

    public RushOrder getRushOrder() { return rushOrder; }
    public void setRushOrder(RushOrder rushOrder) { this.rushOrder = rushOrder; }
} 