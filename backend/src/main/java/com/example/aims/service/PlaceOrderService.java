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
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.mapper.DeliveryInfoMapper;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.Product;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.InvoiceRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.rush.PlaceRushOrderService;

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
    private final CalculateFeeService calculateFeeService;

    public PlaceOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            DeliveryInfoRepository deliveryInfoRepository, PaymentTransactionRepository paymentTransactionRepository,
            InvoiceRepository invoiceRepository, UsersRepository userRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository,
            PlaceRushOrderService placeRushOrderService,
            DeliveryInfoMapper deliveryInfoMapper,
            OrderMapper orderMapper,
            CalculateFeeService calculateFeeService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.placeRushOrderService = placeRushOrderService;
        this.deliveryInfoMapper = deliveryInfoMapper;
        this.orderMapper = orderMapper;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.calculateFeeService = calculateFeeService;
    }

    // public boolean isRushOrderSupported(DeliveryInfoDTO deliveryInfo, List<ProductDTO> products) {
    //     return placeRushOrderService.placeRushOrder(deliveryInfo, products).isSupported();
    // }

    @Transactional
    public OrderDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // // 1. Validate rush order if requested
        // if (orderRequestDTO.getDeliveryInfo().getIsRushOrder() != null && 
        //     orderRequestDTO.getDeliveryInfo().getIsRushOrder()) {
            
        //     List<ProductDTO> products = orderRequestDTO.getCartItems().stream()
        //         .map(CartItemDTO::getProductDTO)
        //         .collect(Collectors.toList());
            
        //     boolean isRushSupported = isRushOrderSupported(orderRequestDTO.getDeliveryInfo(), products);
        //     if (!isRushSupported) {
        //         throw new RuntimeException("Rush order is not supported for the selected products and delivery address.");
        //     }
        // }

        // 2. Tạo và lưu order entity trước để có orderID
        Order order = createNewOrder();

        // 3. Map và lưu delivery info, set orderID
        DeliveryInfo deliveryInfo = createAndSaveDeliveryInfo(order, orderRequestDTO.getDeliveryInfo());

        // 4. Lưu order items
        saveOrderItems(order, orderRequestDTO.getCartItems());

        // 5. Cập nhật tồn kho
        updateProductStocks(orderRequestDTO.getCartItems());

        // 6. Tính tổng tiền sử dụng CalculateFeeService
        double totalAmount = calculateFeeService.calculateTotalPrice(
            orderRequestDTO.getCartItems(), 
            deliveryInfo.getIsRushOrder(), 
            orderRequestDTO.getDeliveryInfo()
        );

        // 7. Gán lại các thuộc tính vào order và lưu lại
        updateOrderWithDeliveryAndTotal(order, deliveryInfo, totalAmount);

        // 8. Trả về DTO
        return orderMapper.toOrderDTO(order);
    }

    private Order createNewOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    private DeliveryInfo createAndSaveDeliveryInfo(Order order, DeliveryInfoDTO deliveryInfoDTO) {
        DeliveryInfo deliveryInfo = deliveryInfoMapper.toEntity(deliveryInfoDTO);
        deliveryInfo.setOrderID(order.getOrderID());
        return deliveryInfoRepository.save(deliveryInfo);
    }

    private void updateOrderWithDeliveryAndTotal(Order order, DeliveryInfo deliveryInfo, double totalAmount) {
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
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

    /**
     * Kiểm tra tính khả dụng của tồn kho cho các sản phẩm trong giỏ hàng
     * @param cartItems Danh sách các sản phẩm trong giỏ hàng
     * @return Danh sách các sản phẩm không đủ tồn kho (rỗng nếu đủ tồn kho)
     */
    public List<CartItemDTO> checkInventoryAvailability(List<CartItemDTO> cartItems) {
        List<CartItemDTO> insufficientItems = new ArrayList<>();
        
        for (CartItemDTO cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductDTO().getProductID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + cartItem.getProductDTO().getProductID()));
            
            // Kiểm tra xem số lượng yêu cầu có vượt quá tồn kho không
            if (cartItem.getQuantity() > product.getQuantity()) {
                // Cập nhật thông tin tồn kho thực tế vào productDTO
                cartItem.getProductDTO().setQuantity(product.getQuantity());
                insufficientItems.add(cartItem);
            }
        }
        
        return insufficientItems;
    }

    /**
     * Kiểm tra xem có đủ tồn kho cho tất cả sản phẩm trong giỏ hàng không
     * @param cartItems Danh sách các sản phẩm trong giỏ hàng
     * @return true nếu đủ tồn kho, false nếu không đủ
     */
    public boolean isInventorySufficient(List<CartItemDTO> cartItems) {
        return checkInventoryAvailability(cartItems).isEmpty();
    }

}
