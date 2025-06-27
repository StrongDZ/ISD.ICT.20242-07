package com.example.aims.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.dto.order.OrderRequestDTO;
import com.example.aims.dto.products.ProductDTO;
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
import com.example.aims.model.Book;
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
    private final ProductRepository productRepository;
    private final PlaceRushOrderService placeRushOrderService;
    private final DeliveryInfoMapper deliveryInfoMapper;
    private final OrderMapper orderMapper;
    private final DeliveryInfoRepository deliveryInfoRepository;

    public PlaceOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            DeliveryInfoRepository deliveryInfoRepository, PaymentTransactionRepository paymentTransactionRepository,
            InvoiceRepository invoiceRepository, UsersRepository userRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository,
            PlaceRushOrderService placeRushOrderService,
            DeliveryInfoMapper deliveryInfoMapper,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.placeRushOrderService = placeRushOrderService;
        this.deliveryInfoMapper = deliveryInfoMapper;
        this.orderMapper = orderMapper;
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    public boolean isRushOrderSupported(DeliveryInfoDTO deliveryInfo, List<ProductDTO> products) {
        return placeRushOrderService.placeRushOrder(deliveryInfo, products).isSupported();
    }

    @Transactional
    public OrderDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // 1. Tạo và lưu order entity trước để có orderID
        Order order = createNewOrder();

        // 2. Map và lưu delivery info, set orderID
        DeliveryInfo deliveryInfo = createAndSaveDeliveryInfo(order, orderRequestDTO.getDeliveryInfo());

        // 3. Lưu order items
        saveOrderItems(order, orderRequestDTO.getCartItems());

        // 4. Cập nhật tồn kho
        updateProductStocks(orderRequestDTO.getCartItems());

        // 5. Tính tổng tiền
        double totalAmount = calculateTotalPrice(orderRequestDTO.getCartItems());

        // 6. Gán lại các thuộc tính vào order và lưu lại
        updateOrderWithDeliveryAndTotal(order, deliveryInfo, totalAmount);

        // 7. Trả về DTO
        return orderMapper.toOrderDTO(order);
    }

    private Order createNewOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    private DeliveryInfo createAndSaveDeliveryInfo(Order order, DeliveryInfoDTO deliveryInfoDTO) {
        DeliveryInfo deliveryInfo = mapDeliveryInfo(deliveryInfoDTO);
        deliveryInfo.setOrderID(order.getOrderID());
        return deliveryInfoRepository.save(deliveryInfo);
    }

    private void updateOrderWithDeliveryAndTotal(Order order, DeliveryInfo deliveryInfo, double totalAmount) {
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    private DeliveryInfo mapDeliveryInfo(DeliveryInfoDTO deliveryInfoDTO) {
        return deliveryInfoMapper.toEntity(deliveryInfoDTO);
    }

    private void saveOrderItems(Order order, List<CartItemDTO> cartItems) {
        for (CartItemDTO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(new OrderItem.OrderItemId(cartItem.getProductDTO().getProductID(), order.getOrderID()));
            orderItem.setProduct(productRepository.findById(cartItem.getProductDTO().getProductID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + cartItem.getProductDTO().getProductID())));
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);
        }
    }

    private void updateProductStocks(List<CartItemDTO> cartItems) {
        for (CartItemDTO cartItem : cartItems) {
            ProductDTO productDTO = cartItem.getProductDTO();
            productDTO.setQuantity(productDTO.getQuantity() - cartItem.getQuantity());
            productRepository.save(productRepository.findById(cartItem.getProductDTO().getProductID())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + cartItem.getProductDTO().getProductID())));
        }
    }

    private double calculateTotalPrice(List<CartItemDTO> cartItems) {
        return cartItems.stream()
            .mapToDouble(item -> item.getProductDTO().getPrice() * item.getQuantity())
            .sum();
    }

}
