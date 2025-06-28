package com.example.aims.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.products.ProductDTO;

/**
 * Service chuyên trách tính toán các loại phí trong hệ thống.
 * 
 * ✅ High Cohesion: Tập trung vào việc tính toán phí
 * ✅ Single Responsibility Principle: Chỉ tính toán phí, không xử lý logic khác
 * ✅ Open/Closed Principle: Có thể mở rộng thêm loại phí mới
 */
@Service
public class CalculateFeeService {

    // Hằng số cho phí giao hàng
    private static final double INITIAL_DELIVERY_FEE = 30000.0; // 30,000 VND cho 0.5kg đầu tiên
    private static final double EXTRA_DELIVERY_FEE_PER_UNIT = 2500.0; // 2,500 VND cho mỗi 0.5kg tiếp theo
    private static final double WEIGHT_UNIT = 0.5; // Đơn vị trọng lượng (kg)
    private static final double FREE_SHIPPING_THRESHOLD = 100000.0; // Ngưỡng miễn phí giao hàng
    private static final double MAX_FREE_SHIPPING_AMOUNT = 25000.0; // Số tiền miễn phí tối đa
    private static final double RUSH_ORDER_FEE_PER_ITEM = 10000.0; // Phí rush order cho mỗi item

    /**
     * Tính tổng giá trị đơn hàng bao gồm tất cả phí
     * 
     * @param cartItems danh sách sản phẩm trong giỏ hàng
     * @param isRushOrder có phải đơn hàng rush không
     * @param deliveryInfo thông tin giao hàng
     * @return tổng giá trị đơn hàng
     */
    public double calculateTotalPrice(List<CartItemDTO> cartItems, boolean isRushOrder, DeliveryInfoDTO deliveryInfo) {
        // Tính tổng giá trị sản phẩm thường (không bao gồm rush order items)
        double subtotal = calculateRegularSubtotal(cartItems);
        
        // Tính tổng giá trị rush order items
        double rushSubtotal = calculateRushSubtotal(cartItems);
        
        // Tính phí giao hàng
        double deliveryFee = calculateDeliveryFee(cartItems, isRushOrder, subtotal);
        
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
    public double calculateDeliveryFee(List<CartItemDTO> cartItems, boolean isRushOrder, double subtotal) {
        // Nếu là rush order, không áp dụng miễn phí giao hàng
        if (isRushOrder) {
            return calculateBaseDeliveryFee(cartItems);
        }
        
        // Kiểm tra điều kiện miễn phí giao hàng (đơn hàng trên 100,000 VND)
        if (subtotal > FREE_SHIPPING_THRESHOLD) {
            double baseFee = calculateBaseDeliveryFee(cartItems);
            // Miễn phí tối đa 25,000 VND
            return Math.max(0, baseFee - MAX_FREE_SHIPPING_AMOUNT);
        }
        
        return calculateBaseDeliveryFee(cartItems);
    }

    /**
     * Tính phí giao hàng cơ bản dựa trên trọng lượng
     */
    public double calculateBaseDeliveryFee(List<CartItemDTO> cartItems) {
        // Tìm sản phẩm nặng nhất
        double maxWeight = cartItems.stream()
            .mapToDouble(item -> {
                Double weight = item.getProductDTO().getWeight();
                return weight != null ? weight : 0.0;
            })
            .max()
            .orElse(0.0);
        
        return calculateDeliveryFeeByWeight(maxWeight);
    }

    /**
     * Tính phí giao hàng dựa trên trọng lượng
     */
    public double calculateDeliveryFeeByWeight(double weightInKg) {
        if (weightInKg <= 0) {
            return 0.0; // Không có trọng lượng
        }
        
        if (weightInKg <= WEIGHT_UNIT) {
            return INITIAL_DELIVERY_FEE;
        } else {
            // Tính thêm cho phần vượt quá 0.5kg
            double extraWeight = weightInKg - WEIGHT_UNIT;
            int extraUnits = (int) Math.ceil(extraWeight / WEIGHT_UNIT);
            return INITIAL_DELIVERY_FEE + (extraUnits * EXTRA_DELIVERY_FEE_PER_UNIT);
        }
    }

    /**
     * Tính phí rush order (10,000 VND cho mỗi rush order item)
     */
    public double calculateRushOrderFee(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .filter(item -> Boolean.TRUE.equals(item.getProductDTO().getEligible()))
            .mapToDouble(item -> RUSH_ORDER_FEE_PER_ITEM * item.getQuantity())
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
        return subtotal > FREE_SHIPPING_THRESHOLD;
    }

    /**
     * Tính số tiền được miễn phí giao hàng
     */
    public double calculateFreeShippingAmount(double baseDeliveryFee) {
        return Math.min(baseDeliveryFee, MAX_FREE_SHIPPING_AMOUNT);
    }
} 