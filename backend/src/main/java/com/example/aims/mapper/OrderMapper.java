package com.example.aims.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.model.Order;

@Component
public class OrderMapper {
    @Autowired
    DeliveryInfoMapper deliveryInfoMapper;

    /**
     * Converts an Order entity to an OrderResponseDTO.
     *
     * @param order the Order entity to convert
     * @return the converted OrderResponseDTO
     */
    public PaymentOrderResponseFromReturnDTO toPaymentOrderResponseFromReturnDTO(Order order) {
        if (order == null)
            return null;

        PaymentOrderResponseFromReturnDTO dto = new PaymentOrderResponseFromReturnDTO();
        dto.setOrderID(order.getOrderID());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentType(order.getPaymentType());

        if (order.getDeliveryInfo() != null) {
            dto.setDeliveryInfo(deliveryInfoMapper.toDto(order.getDeliveryInfo()));
        }

        return dto;
    }

    /**
     * Converts an Order entity to an OrderDTO.
     *
     * @param order the Order entity to convert
     * @return the converted OrderDTO
     */
    public OrderDTO toOrderDTO(Order order) {
        if (order == null)
            return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getOrderID());
        dto.setTotalPrice(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentType(order.getPaymentType());
        // dto.setCustomerID(order.getCustomer().getId());
        dto.setDeliveryInfo(deliveryInfoMapper.toDto(order.getDeliveryInfo()));
        // dto.setItems(order.get);

        if (order.getDeliveryInfo() != null) {
            dto.setDeliveryInfo(deliveryInfoMapper.toDto(order.getDeliveryInfo()));
        }
        return dto;
    }

    /**
     * Converts an Order entity to an OrderRequestDTO.
     *
     * @param order the Order entity to convert
     * @return the converted OrderRequestDTO
     */
    public PaymentOrderRequestDTO toPaymentOrderRequestDTO(Order order) {
        if (order == null)
            return null;

        PaymentOrderRequestDTO dto = new PaymentOrderRequestDTO();
        dto.setAmount(order.getTotalAmount());
        dto.setContent(
                "Payment for order: " + order.getOrderID() + "Order created by: " + order.getDeliveryInfo().getMail());
        dto.setOrderId(order.getOrderID());
        return dto;
    }

    /**
     * Converts an Order entity to an OrderDTO.
     * This is an alias for toOrderDTO method for consistency.
     *
     * @param order the Order entity to convert
     * @return the converted OrderDTO
     */
    public OrderDTO toDTO(Order order) {
        return toOrderDTO(order);
    }
}
