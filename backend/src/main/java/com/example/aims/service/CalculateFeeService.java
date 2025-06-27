package com.example.aims.service;

import com.example.aims.config.DeliveryFeeProperties;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryFeeResponseDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service chuyên trách tính toán các loại phí trong hệ thống.
 * Chứa logic để tính phí vận chuyển cho cả đơn hàng thường và đơn hàng hỏa tốc.
 */
@Service
public class CalculateFeeService {

    private DeliveryFeeProperties deliveryFeeProperties;

    public CalculateFeeService(DeliveryFeeProperties deliveryFeeProperties) {
        this.deliveryFeeProperties=deliveryFeeProperties;
    }
        /**
     * Tính tổng giá trị cuối cùng của đơn hàng, bao gồm tiền hàng và tất cả các loại phí vận chuyển.
     * Phương thức này được sử dụng khi đặt hàng (place order).
     *
     * @param cartItems    Danh sách sản phẩm trong giỏ hàng.
     * @param deliveryInfo Thông tin giao hàng.
     * @return Tổng số tiền khách hàng phải thanh toán.
     */
    public double calculateTotalPrice(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        // 1. Tính tổng giá trị của tất cả sản phẩm trong giỏ hàng.
        double totalSubtotal = calculateSubtotalForItems(cartItems);

        // 2. Tính tất cả các loại phí vận chuyển (cả thường và hỏa tốc).
        DeliveryFeeResponseDTO shippingFees = calculateAllShippingFees(cartItems, deliveryInfo);
        double totalShippingFee = shippingFees.getRegularShippingFee() + shippingFees.getRushShippingFee();

        // 3. Tổng cộng = tiền hàng + tổng phí vận chuyển.
        return totalSubtotal + totalShippingFee;
    }

    /**
     * Phương thức chính, điều phối việc tính toán phí vận chuyển.
     * Tự động chọn logic phù hợp dựa trên lựa chọn của khách hàng.
     *
     * @param cartItems    Danh sách sản phẩm trong giỏ hàng.
     * @param deliveryInfo Thông tin giao hàng (chứa cờ isRushOrder).
     * @return một đối tượng DeliveryFeeResponseDTO chứa các loại phí đã tính.
     */
    public DeliveryFeeResponseDTO calculateAllShippingFees(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        boolean isRushOrderRequested = deliveryInfo != null && Boolean.TRUE.equals(deliveryInfo.getIsRushOrder());

        if (isRushOrderRequested) {
            // Gọi hàm xử lý riêng cho đơn hàng hỏa tốc
            return handleRushOrderCalculation(cartItems, deliveryInfo);
        } else {
            // Gọi hàm xử lý riêng cho đơn hàng thường
            return handleRegularOrderCalculation(cartItems, deliveryInfo);
        }
    }

    /**
     * Xử lý logic tính toán cho đơn hàng GIAO HÀNG THƯỜNG.
     * Coi tất cả sản phẩm là một nhóm và áp dụng free ship nếu đủ điều kiện.
     */
    private DeliveryFeeResponseDTO handleRegularOrderCalculation(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        double totalSubtotal = calculateSubtotalForItems(cartItems);
        double baseFee = calculateBaseDeliveryFeeForItems(cartItems, deliveryInfo);

        double finalRegularFee = baseFee;
        if (isEligibleForFreeShipping(totalSubtotal)) {
            double freeShippingAmount = calculateFreeShippingAmount(baseFee);
            finalRegularFee = Math.max(0, baseFee - freeShippingAmount);
        }
        
        return new DeliveryFeeResponseDTO(finalRegularFee, 0.0);
    }

    /**
     * Xử lý logic tính toán cho đơn hàng GIAO HỎA TỐC.
     * Tách 2 nhóm hàng, tính 2 loại phí riêng biệt.
     */
    private DeliveryFeeResponseDTO handleRushOrderCalculation(List<CartItemDTO> cartItems, DeliveryInfoDTO deliveryInfo) {
        List<CartItemDTO> regularItems = cartItems.stream().filter(item -> !isRushItem(item)).collect(Collectors.toList());
        List<CartItemDTO> rushItems = cartItems.stream().filter(this::isRushItem).collect(Collectors.toList());

        // Tính phí cho nhóm hàng thường (có xét free ship)
        double regularSubtotal = calculateSubtotalForItems(regularItems);
        double regularFee = calculateRegularDeliveryFee(regularItems, regularSubtotal, deliveryInfo);

        // Tính tổng chi phí cho nhóm hàng hỏa tốc (phí ship theo cân nặng + phụ phí dịch vụ)
        double rushBaseShippingFee = calculateRushDeliveryFee(rushItems, deliveryInfo);
        double rushServiceSurcharge = calculateRushServiceSurcharge(rushItems);
        double totalRushFee = rushBaseShippingFee + rushServiceSurcharge;

        return new DeliveryFeeResponseDTO(regularFee, totalRushFee);
    }

    // --- CÁC HÀM HELPER ---

    private double calculateRegularDeliveryFee(List<CartItemDTO> regularItems, double regularSubtotal, DeliveryInfoDTO deliveryInfo) {
        if (regularItems == null || regularItems.isEmpty()) {
            return 0.0;
        }
        double baseFee = calculateBaseDeliveryFeeForItems(regularItems, deliveryInfo);
        if (isEligibleForFreeShipping(regularSubtotal)) {
            double freeShippingAmount = calculateFreeShippingAmount(baseFee);
            return Math.max(0, baseFee - freeShippingAmount);
        }
        return baseFee;
    }

    private double calculateRushDeliveryFee(List<CartItemDTO> rushItems, DeliveryInfoDTO deliveryInfo) {
         if (rushItems == null || rushItems.isEmpty()) {
            return 0.0;
        }
        return calculateBaseDeliveryFeeForItems(rushItems, deliveryInfo);
    }

    private double calculateRushServiceSurcharge(List<CartItemDTO> rushItems) {
        if (rushItems == null || rushItems.isEmpty()) {
            return 0.0;
        }
        return rushItems.stream()
                .mapToDouble(item -> deliveryFeeProperties.getRushOrder().getFeePerItem() * item.getQuantity())
                .sum();
    }

    private double calculateBaseDeliveryFeeForItems(List<CartItemDTO> items, DeliveryInfoDTO deliveryInfo) {
        double maxWeight = items.stream()
                .mapToDouble(item -> item.getProductDTO().getWeight() != null ? item.getProductDTO().getWeight() : 0.0)
                .max()
                .orElse(0.0);
        return calculateDeliveryFeeByWeight(maxWeight, deliveryInfo);
    }

    private double calculateSubtotalForItems(List<CartItemDTO> items) {
        if (items == null) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getProductDTO().getPrice() * item.getQuantity())
                .sum();
    }
    
    // (Các hàm helper tính phí theo cân nặng và khu vực giữ nguyên)

    private boolean isRushItem(CartItemDTO item) {
        return item != null && item.getProductDTO() != null && Boolean.TRUE.equals(item.getProductDTO().getEligible());
    }

    private boolean isEligibleForFreeShipping(double subtotal) {
        return subtotal > deliveryFeeProperties.getFreeShipping().getThreshold();
    }
    
    private double calculateFreeShippingAmount(double baseDeliveryFee) {
        return Math.min(baseDeliveryFee, deliveryFeeProperties.getFreeShipping().getMaxAmount());
    }

    private double calculateDeliveryFeeByWeight(double weightInKg, DeliveryInfoDTO deliveryInfo) {
        if (weightInKg <= 0) return 0.0;
        if (isInnerCity(deliveryInfo)) {
            return calculateInnerCityDeliveryFee(weightInKg);
        } else {
            return calculateRegularZoneDeliveryFee(weightInKg);
        }
    }

    private boolean isInnerCity(DeliveryInfoDTO deliveryInfo) {
        if (deliveryInfo == null || deliveryInfo.getCity() == null) return false;
        String city = deliveryInfo.getCity().trim().toLowerCase();
        return city.equals("hà nội") || city.equals("hanoi") || city.equals("tp.hcm") || city.equals("ho chi minh city") || city.equals("thành phố hồ chí minh");
    }

    private double calculateInnerCityDeliveryFee(double weightInKg) {
        double baseWeight = deliveryFeeProperties.getInnerCity().getWeightUnit();
        double initialFee = deliveryFeeProperties.getInnerCity().getInitialFee();
        if (weightInKg <= baseWeight) return initialFee;
        
        double extraWeight = weightInKg - baseWeight;
        double incrementUnit = deliveryFeeProperties.getRegular().getWeightUnit();
        double extraFeePerUnit = deliveryFeeProperties.getRegular().getExtraFeePerUnit();
        int extraUnits = (int) Math.ceil(extraWeight / incrementUnit);
        return initialFee + (extraUnits * extraFeePerUnit);
    }

    private double calculateRegularZoneDeliveryFee(double weightInKg) {
        double baseWeight = deliveryFeeProperties.getRegular().getWeightUnit();
        double initialFee = deliveryFeeProperties.getRegular().getInitialFee();
        if (weightInKg <= baseWeight) return initialFee;

        double extraWeight = weightInKg - baseWeight;
        double incrementUnit = deliveryFeeProperties.getRegular().getWeightUnit();
        double extraFeePerUnit = deliveryFeeProperties.getRegular().getExtraFeePerUnit();
        int extraUnits = (int) Math.ceil(extraWeight / incrementUnit);
        return initialFee + (extraUnits * extraFeePerUnit);
    }
}
