package com.example.aims.service;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.OrderItemDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//***Cohesion: low to medium
// In the case of the OrderService class:

// It handles several different tasks:

// Managing orders (e.g., creating orders, fetching orders by status, updating order status)

// Processing payments (e.g., creating payment transactions)

// Managing inventory (e.g., updating product quantity when an order is placed)

// Handling delivery information (e.g., saving delivery info for an order)

// Generating invoices (e.g., creating an invoice with product prices, VAT, and delivery fees)

// While all of these tasks are related to the concept of an order, they don't form a single responsibility. 
//So, the class lacks high cohesion because it mixes different concerns (order management, payment, inventory, delivery, invoice) within the same class.
// As a result, the cohesion is low to medium.

//***SRP  Violation:
// In the case of OrderService, it violates SRP because it is taking on multiple responsibilities:

// Order Creation: It creates new orders, manages the order status, and associates products with the order.

// Payment Handling: It creates and processes payment transactions for the orders.

// Inventory Management: It updates the quantity of products in the inventory when an order is created.

// Delivery Information: It handles the delivery details for each order (address, recipient, etc.).

// Invoice Generation: It generates invoices based on order items, including VAT and delivery fees.

// These responsibilities should ideally be split into separate classes or services.

// ***The solution:
// To improve both cohesion and adhere to SRP, you can refactor the OrderService class into several smaller services that each handle one responsibility:

// OrderCreationService: Handles everything related to order creation.

// PaymentService: Manages payment transactions.

// InventoryService: Manages inventory and stock updates.

// DeliveryService: Handles delivery details and logistics.

// InvoiceService: Generates invoices for orders.

// By doing this,  reducing the complexity of the OrderService class and ensure that each service has a clear and focused responsibility.

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

    // public Invoice createInvoice()
    public List<OrderDTO> getCustomerOrders(Integer customerId) {
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

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getOrderID());
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
            deliveryInfoDTO.setCity(deliveryInfo.getCity());
            deliveryInfoDTO.setDistrict(deliveryInfo.getDistrict());
            deliveryInfoDTO.setAddressDetail(deliveryInfo.getAddressDetail());
            deliveryInfoDTO.setPhoneNumber(deliveryInfo.getPhoneNumber());
            deliveryInfoDTO.setRecipientName(deliveryInfo.getRecipientName());
            deliveryInfoDTO.setMail(deliveryInfo.getMail());

            dto.setDeliveryInfo(deliveryInfoDTO);
        });

        // Get total price from invoice
        invoiceRepository.findById(order.getOrderID()).ifPresent(invoice -> {
            dto.setTotalPrice(invoice.getProductPriceIncludingVAT() + invoice.getDeliveryFee());
        });

        return dto;
    }
}