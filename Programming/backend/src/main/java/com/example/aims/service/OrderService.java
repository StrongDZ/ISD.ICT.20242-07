package com.example.aims.service;

import com.example.aims.dto.DeliveryInfoDTO;
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
    public OrderDTO createOrderFromCart(String customerId, DeliveryInfoDTO deliveryInfoDTO) {
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Users not found with id: " + customerId));
        
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Create order
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setStatus("PENDING");
        
        orderRepository.save(order);
        
        // Create order items
        double totalPrice = 0.0f;
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            OrderItem.OrderItemId orderItemId = new OrderItem.OrderItemId(cartItem.getProduct().getProductID(), orderId);
            orderItem.setId(orderItemId);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            
            orderItemRepository.save(orderItem);
            
            // Update product quantity
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            
            totalPrice += product.getPrice() * cartItem.getQuantity();
        }
        
        // Create delivery info
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setOrderID(orderId);
        deliveryInfo.setOrder(order);
        deliveryInfo.setDeliveryAddress(deliveryInfoDTO.getDeliveryAddress());
        deliveryInfo.setPhoneNumber(deliveryInfoDTO.getPhoneNumber());
        deliveryInfo.setRecipientName(deliveryInfoDTO.getRecipientName());
        deliveryInfo.setMail(deliveryInfoDTO.getMail());
        deliveryInfo.setProvince(deliveryInfoDTO.getProvince());
        
        deliveryInfoRepository.save(deliveryInfo);
        
        // Create payment transaction
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setOrderID(orderId);
        paymentTransaction.setOrder(order);
        paymentTransaction.setContent("Order payment");
        paymentTransaction.setDatetime(new Date());
        
        paymentTransactionRepository.save(paymentTransaction);
        
        // Create invoice
        double vat = 0.1f; // 10% VAT
        double deliveryFee = 5.0f; // Fixed delivery fee
        
        Invoice invoice = new Invoice();
        invoice.setOrderID(orderId);
        invoice.setOrder(order);
        invoice.setProductPriceExcludingVAT(totalPrice);
        invoice.setProductPriceIncludingVAT(totalPrice * (1 + vat));
        invoice.setDeliveryFee(deliveryFee);
        
        invoiceRepository.save(invoice);
        
        // Clear the cart
        cartItemRepository.deleteByCustomer(customer);
        
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
        dto.setId(order.getId());
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
        }).collect(Collectors.toList());
        
        dto.setItems(orderItemDTOs);
        
        // Get delivery info
        deliveryInfoRepository.findById(order.getId()).ifPresent(deliveryInfo -> {
            DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
            deliveryInfoDTO.setDeliveryAddress(deliveryInfo.getDeliveryAddress());
            deliveryInfoDTO.setPhoneNumber(deliveryInfo.getPhoneNumber());
            deliveryInfoDTO.setRecipientName(deliveryInfo.getRecipientName());
            deliveryInfoDTO.setMail(deliveryInfo.getMail());
            deliveryInfoDTO.setProvince(deliveryInfo.getProvince());
            
            dto.setDeliveryInfo(deliveryInfoDTO);
        });
        
        // Get total price from invoice
        invoiceRepository.findById(order.getId()).ifPresent(invoice -> {
            dto.setTotalPrice(invoice.getProductPriceIncludingVAT() + invoice.getDeliveryFee());
        });
        
        return dto;
    }
}