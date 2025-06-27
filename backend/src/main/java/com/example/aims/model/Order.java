package com.example.aims.model;

import jakarta.persistence.*;
import com.example.aims.common.OrderStatus;

// Functional Cohesion – All methods and fields are related to the single
// responsibility: managing an order
// SRP respected – The class handles only order-related logic

// ✅ SOLID Principles Evaluation for Order class

// ✅ SRP – Single Responsibility Principle:
// The class is focused solely on Order-related data and basic status logic. No
// unrelated logic is embedded.

// ✅ OCP – Open/Closed Principle:
// The status check and change logic is embedded directly. Adding new statuses
// requires modifying existing methods.

// ✅ LSP – Liskov Substitution Principle:
// No inheritance used, so the principle is not violated.

// ✅ ISP – Interface Segregation Principle:
// No interface is implemented, but if one is added in future, ensure it is
// segregated based on purpose (e.g., ReadOnlyOrder, StatusChangeable).
// ➤ ISP is currently not applicable, but should be kept in mind for future
// extensions.

// ❌ DIP – Dependency Inversion Principle:
// This entity class depends directly on `Users` (another entity), which is
// acceptable for data model layer.
// However, if business logic becomes more complex, consider using services for
// decision-making logic instead of embedding it here.
// ➤ Slight DIP violation risk if logic grows. Keep data and logic
// responsibilities separate.

@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderID;

    // @ManyToOne
    // @JoinColumn(name = "customerID")
    // private Users customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "total_amount")
    private Double totalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderID")
    private DeliveryInfo deliveryInfo;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "rejected_reason")
    private String rejectedReason;

    // Constructors
    public Order() {
    }

    public Order(String orderID, OrderStatus status, Double totalAmount, 
                DeliveryInfo deliveryInfo, String paymentType, String rejectedReason) {
        this.orderID = orderID;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryInfo = deliveryInfo;
        this.paymentType = paymentType;
        this.rejectedReason = rejectedReason;
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

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    // Business methods
    public OrderStatus checkOrderStatus() {
        return this.status;
    }

    public void changeRejectOrder() {
        this.status = OrderStatus.REJECTED;
    }

    public void changeApproveOrder() {
        this.status = OrderStatus.APPROVED;
    }

    public void updateRejectedReason(String reason) {
        this.rejectedReason = reason;
    }
}