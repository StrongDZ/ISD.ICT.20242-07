package com.example.aims.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderRequestDTO;
import com.example.aims.mapper.DeliveryInfoMapper;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.Product;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.ProductRepository;

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

    private final OrderMapper orderMapper;
    private final CalculateFeeService calculateFeeService;
    private final InventoryService inventoryService;
    private final OrderCreationService orderCreationService;

    public PlaceOrderService(OrderMapper orderMapper,
            CalculateFeeService calculateFeeService,
            InventoryService inventoryService,
            OrderCreationService orderCreationService) {
        this.orderMapper = orderMapper;
        this.calculateFeeService = calculateFeeService;
        this.inventoryService = inventoryService;
        this.orderCreationService = orderCreationService;
    }


    @Transactional
    public OrderDTO placeOrder(OrderRequestDTO orderRequestDTO) {

        // 1. Tạo và lưu order entity trước để có orderID
        Order order = orderCreationService.createNewOrder();

        // 2. Map và lưu delivery info, set orderID
        DeliveryInfo deliveryInfo = orderCreationService.createAndSaveDeliveryInfo(order, orderRequestDTO.getDeliveryInfo());

        // 3. Lưu order items
        orderCreationService.saveOrderItems(order, orderRequestDTO.getCartItems());

        // 4. Cập nhật tồn kho thông qua InventoryService
        inventoryService.updateProductStocks(orderRequestDTO.getCartItems());

        // 5. Tính tổng tiền sử dụng CalculateFeeService
        double totalAmount = calculateFeeService.calculateTotalPrice(
            orderRequestDTO.getCartItems(), 
            orderRequestDTO.getDeliveryInfo()
        );

        // 6. Gán lại các thuộc tính vào order và lưu lại
        orderCreationService.updateOrderWithDeliveryAndTotal(order, deliveryInfo, totalAmount);

        // 7. Trả về DTO
        return orderMapper.toOrderDTO(order);
    }

    /**
     * Kiểm tra tính khả dụng của tồn kho cho các sản phẩm trong giỏ hàng
     * @param cartItems Danh sách các sản phẩm trong giỏ hàng
     * @return Danh sách các sản phẩm không đủ tồn kho (rỗng nếu đủ tồn kho)
     */
    public List<CartItemDTO> checkInventoryAvailability(List<CartItemDTO> cartItems) {
        return inventoryService.checkInventoryAvailability(cartItems);
    }

    /**
     * Kiểm tra xem có đủ tồn kho cho tất cả sản phẩm trong giỏ hàng không
     * @param cartItems Danh sách các sản phẩm trong giỏ hàng
     * @return true nếu đủ tồn kho, false nếu không đủ
     */
    public boolean isInventorySufficient(List<CartItemDTO> cartItems) {
        return inventoryService.isInventorySufficient(cartItems);
    }

}
