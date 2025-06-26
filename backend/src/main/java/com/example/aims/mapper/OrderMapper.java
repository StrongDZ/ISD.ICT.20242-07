package com.example.aims.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.order.PaymentOrderRetrievalDTO;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.model.Order;

@Component
public class OrderMapper {
    @Autowired DeliveryInfoMapper deliveryInfoMapper;

    @Autowired UsersMapper customerMapper;

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


        if (order.getDeliveryInfo() != null) {
            dto.setDeliveryInfo(deliveryInfoMapper.toDto(order.getDeliveryInfo()));
        }

        if (order.getCustomer() != null) {
            dto.setCustomer(customerMapper.toDto(order.getCustomer()));
        }
        return dto;
    }

    /**
     * Converts an Order entity to a PaymentOrderResponseDTO.
     * 
     * @param order
     * @return PaymentOrderResponseDTO
     */
    public PaymentOrderRetrievalDTO toPaymentOrderRetrievalDTO(Order order) {
        if (order == null)
            return null;

        PaymentOrderRetrievalDTO dto = new PaymentOrderRetrievalDTO();
        dto.setOrderID(order.getOrderID());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());


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
        dto.setCustomerID(order.getCustomer().getId());
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
                "Payment for order: " + order.getOrderID() + "Order created by: " + order.getCustomer().getGmail());
        dto.setOrderId(order.getOrderID());
        return dto;
    }
}
