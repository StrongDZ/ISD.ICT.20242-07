package com.example.aims.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.dto.order.OrderRequestDTO;
import com.example.aims.mapper.DeliveryInfoMapper;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.model.CartItem;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Invoice;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.InvoiceRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.rush.PlaceRushOrderService;
import com.example.aims.dto.products.ProductDTO;

//***Cohesion: low to medium
// In the case of the PlaceOrderService class:
//
// It handles several different tasks:
//
// - Creating orders (e.g., saving new order entities with delivery info)
// - Calculating total amount (subtotal + VAT + delivery fee)
// - Saving delivery information
// - Linking products to orders (ProductOrderEntity creation)
// - Supporting rush order checking based on address and product property
//
// These tasks are all related to placing an order, but they represent distinct responsibilities.
// So the class has low to medium cohesion because it blends different concerns (order placement logic, pricing logic, delivery logic, and business rule checking for rush orders).

//***SRP Violation:
// This class violates the Single Responsibility Principle (SRP) by handling multiple responsibilities:
//
// - Business Logic: Checks if rush delivery is supported for a province and product.
// - Delivery Handling: Saves delivery info to the database.
// - Order Management: Creates orders and links them to products.
// - Pricing Calculation: Calculates total amount from VAT, subtotal, and delivery fee.
// 
// These should ideally be separated into smaller services or helpers focused on:
// - RushOrderChecker
// - DeliveryService
// - OrderCreationService
// - InvoiceCalculationService

//***The solution:
// To improve cohesion and follow SRP, refactor the PlaceOrderService into dedicated services:
//
// - OrderCreationService: For creating and saving orders.
// - DeliveryService: For managing and saving delivery information.
// - ProductOrderService: For handling associations between products and orders.
// - RushOrderService: To encapsulate rush order support checking.
// - PricingService: To calculate total amounts including taxes and delivery.
//
// This modular approach simplifies testing, debugging, and code reusability while keeping responsibilities clearly separated.

@Service
public class PlaceOrderService {

    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final PlaceRushOrderService placeRushOrderService;
    private final DeliveryInfoMapper deliveryInfoMapper;
    private final OrderMapper orderMapper;

    public PlaceOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            DeliveryInfoRepository deliveryInfoRepository, PaymentTransactionRepository paymentTransactionRepository,
            InvoiceRepository invoiceRepository, UsersRepository userRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository,
            PlaceRushOrderService placeRushOrderService,
            DeliveryInfoMapper deliveryInfoMapper,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.placeRushOrderService = placeRushOrderService;
        this.deliveryInfoMapper = deliveryInfoMapper;
        this.orderMapper = orderMapper;
    }

    public boolean isRushOrderSupported(DeliveryInfoDTO deliveryInfo, List<ProductDTO> products) {
        return placeRushOrderService.placeRushOrder(deliveryInfo, products).isSupported();
    }

    @Transactional
    public OrderDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // 1. Map delivery info
        DeliveryInfo deliveryInfo = deliveryInfoMapper.toEntity(orderRequestDTO.getDeliveryInfo());

        // 2. Tạo order entity
        Order order = new Order();
        order.setDeliveryInfo(deliveryInfo);
        order.setStatus(OrderStatus.PENDING);
        // Nếu có customer, set customer ở đây (ví dụ: order.setCustomer(...))

        // 3. Lưu order trước để lấy orderID (nếu cần)
        order = orderRepository.save(order);

        // 4. Xử lý order item và cập nhật tồn kho
        saveOrderItem(order,orderRequestDTO.getCartItems());
        updateProductStock(orderRequestDTO.getCartItems());
        // 5. Tính tổng tiền
        double totalAmount = calculateTotalPrice(orderRequestDTO.getCartItems());
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // 6. Trả về DTO
        return orderMapper.toOrderDTO(order);
    }


    private void saveOrderItem(Order order, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();
        OrderItem orderItem = new OrderItem();
        orderItem.setId(new OrderItem.OrderItemId(product.getProductID(), order.getOrderID()));
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItemRepository.save(orderItem);
        }
    }

    private void updateProductStock(List<CartItem> cartItems) {
        for(CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

    }

    private double calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    }


}
