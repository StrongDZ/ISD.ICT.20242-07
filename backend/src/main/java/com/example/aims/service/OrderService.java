package com.example.aims.service;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.OrderItemDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                       DeliveryInfoRepository deliveryInfoRepository, PaymentTransactionRepository paymentTransactionRepository,
                       InvoiceRepository invoiceRepository, UsersRepository userRepository,
                       ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<OrderDTO> getCustomerOrders(String customerId) {
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Users not found with id: " + customerId));
        
        List<Order> orders = orderRepository.findByCustomer(customer);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        
        for (Order order : orders) {
            OrderDTO dto = convertToDTO(order);
            orderDTOs.add(dto);
        }
        
        return orderDTOs;
    }

    public OrderDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        return convertToDTO(order);
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        
        for (Order order : orders) {
            OrderDTO dto = convertToDTO(order);
            orderDTOs.add(dto);
        }
        
        return orderDTOs;
    }

    @Transactional
    public OrderDTO createOrder(String customerId, InvoiceDTO invoiceDTO) {
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Users not found with id: " + customerId));
        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("PENDING");
        
        orderRepository.save(order);
        
        // Create order items
        double totalPrice = 0.0f;
        
        for (DeliveryProductDTO deliveryProduct : invoiceDTO.getCart()) {
            Product product = productRepository.findById(deliveryProduct.getId()).orElseThrow(() -> new RuntimeException("Product not found with id: " + deliveryProduct.getId()));
            
            OrderItem orderItem = new OrderItem();
            OrderItem.OrderItemId orderItemId = new OrderItem.OrderItemId(deliveryProduct.getId(), order.getOrderID());
            orderItem.setId(orderItemId);
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(deliveryProduct.getQuantity());
            
            orderItemRepository.save(orderItem);
            
            productRepository.updateProductQuantity(deliveryProduct.getId(), product.getQuantity() - deliveryProduct.getQuantity());
            
            totalPrice += product.getPrice() * deliveryProduct.getQuantity();
        }

        
        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        orderRepository.save(order);
        
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());
        dto.setCustomerID(order.getCustomer().getId());
        dto.setStatus(order.getStatus());
        
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductID(item.getProduct().getProductID());
            itemDTO.setProductTitle(item.getProduct().getTitle());
            itemDTO.setProductPrice(item.getProduct().getPrice());
            itemDTO.setQuantity(item.getQuantity());
            return itemDTO;
        }).collect(Collectors.toList());
        
        dto.setItems(orderItemDTOs);
        
        // Get delivery info
        deliveryInfoRepository.findById(order.getOrderID()).ifPresent(deliveryInfo -> {
            DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
            deliveryInfoDTO.setDeliveryAddress(deliveryInfo.getDeliveryAddress());
            deliveryInfoDTO.setPhoneNumber(deliveryInfo.getPhoneNumber());
            deliveryInfoDTO.setRecipientName(deliveryInfo.getRecipientName());
            deliveryInfoDTO.setMail(deliveryInfo.getMail());
            deliveryInfoDTO.setProvince(deliveryInfo.getProvince());
            
            dto.setDeliveryInfo(deliveryInfoDTO);
        });
        
        // Get total price from invoice
            invoiceRepository.findById(order.getOrderID()).ifPresent(invoice -> {
            dto.setTotalPrice(invoice.getProductPriceIncludingVAT() + invoice.getDeliveryFee());
        });
        
        return dto;
    }
}