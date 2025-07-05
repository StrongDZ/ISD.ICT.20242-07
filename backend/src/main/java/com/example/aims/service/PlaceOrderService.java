package com.example.aims.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderRequestDTO;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;

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