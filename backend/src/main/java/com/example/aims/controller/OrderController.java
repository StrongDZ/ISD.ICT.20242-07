package com.example.aims.controller;

import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.dto.order.OrderApprovalRequestDTO;
import com.example.aims.dto.order.OrderRejectionRequestDTO;
import com.example.aims.dto.order.OrderManagementResponseDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.service.IOrderManagementService;
import com.example.aims.common.OrderStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Order", description = "Order management APIs")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final IOrderManagementService orderManagementService;

    public OrderController(OrderRepository orderRepository, OrderItemRepository orderItemRepository, IOrderManagementService orderManagementService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderManagementService = orderManagementService;
    }

    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders for manager dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @Operation(summary = "Get orders by status", description = "Retrieves orders filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered orders"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id) {
        return orderRepository.findByOrderID(id)
                .map(order -> ResponseEntity.ok(convertToDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Approve order", description = "Approves a pending order by changing its status to APPROVED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order approved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or order cannot be approved"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<OrderManagementResponseDTO> approveOrder(
            @PathVariable String id,
            @RequestBody(required = false) OrderApprovalRequestDTO approvalRequest) {
        
        // Create request if not provided
        if (approvalRequest == null) {
            approvalRequest = new OrderApprovalRequestDTO();
        }
        approvalRequest.setOrderId(id);
        
        OrderManagementResponseDTO response = orderManagementService.approveOrder(approvalRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Reject order", description = "Rejects a pending order by changing its status to REJECTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or order cannot be rejected"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<OrderManagementResponseDTO> rejectOrder(
            @PathVariable String id,
            @RequestBody OrderRejectionRequestDTO rejectionRequest) {
        
        rejectionRequest.setOrderId(id);
        
        OrderManagementResponseDTO response = orderManagementService.rejectOrder(rejectionRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Check order eligibility", description = "Checks if an order can be approved or rejected")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked order eligibility"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/eligible")
    public ResponseEntity<Boolean> isOrderEligible(@PathVariable String id) {
        boolean eligible = orderManagementService.isOrderEligibleForManagement(id);
        return ResponseEntity.ok(eligible);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getOrderID());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalAmount());
        dto.setOrderDate(new java.util.Date()); // Use current date since createdAt field doesn't exist
        dto.setPaymentMethod("Cash on Delivery"); // Default payment method
        
        // Convert DeliveryInfo if exists
        if (order.getDeliveryInfo() != null) {
            DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
            deliveryInfoDTO.setAddressDetail(order.getDeliveryInfo().getAddressDetail());
            deliveryInfoDTO.setPhoneNumber(order.getDeliveryInfo().getPhoneNumber());
            deliveryInfoDTO.setRecipientName(order.getDeliveryInfo().getRecipientName());
            deliveryInfoDTO.setMail(order.getDeliveryInfo().getMail());
            deliveryInfoDTO.setCity(order.getDeliveryInfo().getCity());
            deliveryInfoDTO.setDistrict(order.getDeliveryInfo().getDistrict());
            deliveryInfoDTO.setIsRushOrder(order.getDeliveryInfo().getIsRushOrder());
            dto.setDeliveryInfo(deliveryInfoDTO);
            dto.setIsRushOrder(order.getDeliveryInfo().getIsRushOrder());
        }
        
        // Load and convert order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        dto.setItems(orderItemDTOs);
        
        return dto;
    }
    
    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductID(orderItem.getProduct().getProductID());
        dto.setProductTitle(orderItem.getProduct().getTitle());
        dto.setProductPrice(orderItem.getProduct().getPrice());
        dto.setQuantity(orderItem.getQuantity());
        return dto;
    }
} 