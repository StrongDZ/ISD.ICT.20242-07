package com.example.aims.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aims.config.DeliveryFeeProperties;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;

/**
 * Service chuyên trách tính toán các loại phí trong hệ thống.
 * 
 * ✅ High Cohesion: Tập trung vào việc tính toán phí
 * ✅ Single Responsibility Principle: Chỉ tính toán phí, không xử lý logic khác
 * ✅ Open/Closed Principle: Có thể mở rộng thêm loại phí mới
 */
@Service
public class CalculateFeeService {

    @Autowired
    private DeliveryFeeProperties deliveryFeeProperties;

    /**
     * Tính tổng giá trị đơn hàng bao gồm tất cả phí
     * 
     * @param cartItems danh sách sản phẩm trong giỏ hàng
     * @param deliveryInfo thông tin giao hàng
     * @return tổng giá trị đơn hàng
     */
    public double calculateTotalPrice(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        // Tính tổng giá trị sản phẩm thường (không bao gồm rush order items)
        double subtotal = calculateRegularSubtotal(cartItems);
        // Tính tổng giá trị rush order items
        double rushSubtotal = calculateRushSubtotal(cartItems);
        // Tính phí giao hàng
        double deliveryFee = calculateDeliveryFee(cartItems, subtotal, deliveryInfo);
        // Tính phí rush order
        double rushOrderFee = calculateRushOrderFee(cartItems);
        // Tổng cộng
        return subtotal + rushSubtotal + deliveryFee + rushOrderFee;
    }

    /**
     * Tính tổng giá trị sản phẩm thường (không rush eligible)
     */
    public double calculateRegularSubtotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .filter(item -> !Boolean.TRUE.equals(item.getProductDTO().getEligible()))
            .mapToDouble(item -> item.getProductDTO().getPrice() * item.getQuantity())
            .sum();
    }

    /**
     * Tính tổng giá trị sản phẩm rush order
     */
    public double calculateRushSubtotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .filter(item -> Boolean.TRUE.equals(item.getProductDTO().getEligible()))
            .mapToDouble(item -> item.getProductDTO().getPrice() * item.getQuantity())
            .sum();
    }

    /**
     * Tính phí giao hàng
     */
    public double calculateDeliveryFee(List<CartItemDTO> cartItems, double subtotal, DeliveryInfoDTO deliveryInfo) {
        boolean isRushOrder = deliveryInfo != null && Boolean.TRUE.equals(deliveryInfo.getIsRushOrder());
        // Nếu là rush order, không áp dụng miễn phí giao hàng
        if (isRushOrder) {
            return calculateBaseDeliveryFee(cartItems, deliveryInfo);
        }
        // Kiểm tra điều kiện miễn phí giao hàng (đơn hàng trên 100,000 VND)
        if (subtotal > deliveryFeeProperties.getFreeShipping().getThreshold()) {
            double baseFee = calculateBaseDeliveryFee(cartItems, deliveryInfo);
            // Miễn phí tối đa 25,000 VND
            return Math.max(0, baseFee - deliveryFeeProperties.getFreeShipping().getMaxAmount());
        }
        return calculateBaseDeliveryFee(cartItems, deliveryInfo);
    }

    /**
     * Tính phí giao hàng cơ bản dựa trên trọng lượng và địa chỉ
     */
    public double calculateBaseDeliveryFee(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        // Tìm sản phẩm nặng nhất
        double maxWeight = cartItems.stream()
            .mapToDouble(item -> {
                Double weight = item.getProductDTO().getWeight();
                return weight != null ? weight : 0.0;
            })
            .max()
            .orElse(0.0);
        
        return calculateDeliveryFeeByWeight(maxWeight, deliveryInfo);
    }

    /**
     * Tính phí giao hàng dựa trên trọng lượng và địa chỉ
     */
    public double calculateDeliveryFeeByWeight(double weightInKg, DeliveryInfoDTO deliveryInfo) {
        if (weightInKg <= 0) {
            return 0.0; // Không có trọng lượng
        }
        
        // Kiểm tra xem có phải nội thành Hà Nội hoặc TP.HCM không
        if (isInnerCity(deliveryInfo)) {
            return calculateInnerCityDeliveryFee(weightInKg);
        } else {
            return calculateRegularDeliveryFee(weightInKg);
        }
    }

    /**
     * Tính phí giao hàng cho nội thành Hà Nội và TP.HCM
     */
    private double calculateInnerCityDeliveryFee(double weightInKg) {
        if (weightInKg <= deliveryFeeProperties.getInnerCity().getWeightUnit()) {
            return deliveryFeeProperties.getInnerCity().getInitialFee();
        } else {
            // Tính thêm cho phần vượt quá 3kg
            double extraWeight = weightInKg - deliveryFeeProperties.getInnerCity().getWeightUnit();
            int extraUnits = (int) Math.ceil(extraWeight / deliveryFeeProperties.getRegular().getWeightUnit());
            return deliveryFeeProperties.getInnerCity().getInitialFee() + (extraUnits * deliveryFeeProperties.getInnerCity().getExtraFeePerUnit());
        }
    }

    /**
     * Tính phí giao hàng cho khu vực thường
     */
    private double calculateRegularDeliveryFee(double weightInKg) {
        if (weightInKg <= deliveryFeeProperties.getRegular().getWeightUnit()) {
            return deliveryFeeProperties.getRegular().getInitialFee();
        } else {
            // Tính thêm cho phần vượt quá 0.5kg
            double extraWeight = weightInKg - deliveryFeeProperties.getRegular().getWeightUnit();
            int extraUnits = (int) Math.ceil(extraWeight / deliveryFeeProperties.getRegular().getWeightUnit());
            return deliveryFeeProperties.getRegular().getInitialFee() + (extraUnits * deliveryFeeProperties.getRegular().getExtraFeePerUnit());
        }
    }

    /**
     * Kiểm tra xem địa chỉ có phải nội thành Hà Nội hoặc TP.HCM không
     */
    private boolean isInnerCity(DeliveryInfoDTO deliveryInfo) {
        if (deliveryInfo == null || deliveryInfo.getCity() == null) {
            return false;
        }
        
        String city = deliveryInfo.getCity().trim();
        
        // Kiểm tra Hà Nội
        if (city.equalsIgnoreCase("Hà Nội") || city.equalsIgnoreCase("Hanoi")) {
            return true;
        }
        
        // Kiểm tra TP.HCM
        if (city.equalsIgnoreCase("TP.HCM") || city.equalsIgnoreCase("Ho Chi Minh City") || 
            city.equalsIgnoreCase("Thành phố Hồ Chí Minh")) {
            return true;
        }
        
        return false;
    }

    /**
     * Tính phí rush order (10,000 VND cho mỗi rush order item)
     */
    public double calculateRushOrderFee(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .filter(item -> Boolean.TRUE.equals(item.getProductDTO().getEligible()))
            .mapToDouble(item -> deliveryFeeProperties.getRushOrder().getFeePerItem() * item.getQuantity())
            .sum();
    }

    /**
     * Tính tổng giá trị đơn hàng (chỉ sản phẩm, không bao gồm phí)
     */
    public double calculateSubtotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .mapToDouble(item -> item.getProductDTO().getPrice() * item.getQuantity())
            .sum();
    }

    /**
     * Kiểm tra xem đơn hàng có được miễn phí giao hàng không
     */
    public boolean isEligibleForFreeShipping(double subtotal) {
        return subtotal > deliveryFeeProperties.getFreeShipping().getThreshold();
    }

    /**
     * Tính số tiền được miễn phí giao hàng
     */
    public double calculateFreeShippingAmount(double baseDeliveryFee) {
        return Math.min(baseDeliveryFee, deliveryFeeProperties.getFreeShipping().getMaxAmount());
    }
} 