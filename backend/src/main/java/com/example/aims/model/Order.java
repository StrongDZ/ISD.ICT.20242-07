package com.example.aims.model;

import jakarta.persistence.*;
import com.example.aims.common.OrderStatus;

/**
 * Entity class for Order
 * After refactoring:
 * - Follows SRP by only handling order data storage
 * - Status management is delegated to OrderStatusManager
 */
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "total_amount")
    private Double totalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderID")
    private DeliveryInfo deliveryInfo;

    @Column(name = "rejected_reason")
    private String rejectedReason;

    @Transient
    private OrderStatusManager statusManager;

    // Constructors
    public Order() {
        this.statusManager = new OrderStatusManager(this);
    }

    public Order(String orderID, OrderStatus status, Double totalAmount, 
                DeliveryInfo deliveryInfo, String rejectedReason) {
        this.orderID = orderID;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryInfo = deliveryInfo;
        this.rejectedReason = rejectedReason;
        this.statusManager = new OrderStatusManager(this);
    }

    // Getters and Setters
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    // Status management methods now delegate to OrderStatusManager
    public void approveOrder() {
        statusManager.approve();
    }

    public void rejectOrder(String reason) {
        statusManager.reject(reason);
    }

    public OrderStatus checkOrderStatus() {
        return statusManager.getCurrentStatus();
    }
}