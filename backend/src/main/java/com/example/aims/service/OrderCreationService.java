package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.mapper.DeliveryInfoMapper;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.Product;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCreationService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final DeliveryInfoMapper deliveryInfoMapper;

    @Transactional
    public Order createNewOrder() {
        
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        
        return orderRepository.save(order);
        }

    @Transactional
    public DeliveryInfo createAndSaveDeliveryInfo(Order order, DeliveryInfoDTO deliveryInfoDTO) {
        
        DeliveryInfo deliveryInfo = deliveryInfoMapper.toEntity(deliveryInfoDTO);
        deliveryInfo.setOrder(order);
        
        return deliveryInfoRepository.save(deliveryInfo);
                
    }

    @Transactional
    public void saveOrderItems(Order order, List<CartItemDTO> cartItems) {
        
        for (CartItemDTO cartItem : cartItems) {
            OrderItem orderItem = createOrderItem(order, cartItem);
            orderItemRepository.save(orderItem);

        }
        
    }

    @Transactional
    public void updateOrderWithDeliveryAndTotal(Order order, DeliveryInfo deliveryInfo, double totalAmount) {
        
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount(totalAmount);
        
        orderRepository.save(order);
        
    }

    public OrderItem createOrderItem(Order order, CartItemDTO cartItem) {
        String productId = cartItem.getProductDTO().getProductID();
        
        // Tìm product trong database
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
        
        // Tạo order item
        OrderItem orderItem = new OrderItem();
        orderItem.setId(new OrderItem.OrderItemId(productId, order.getOrderID()));
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
                
        return orderItem;
    }
} 