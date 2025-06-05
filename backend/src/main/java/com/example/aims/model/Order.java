package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    private String orderID;

    @ManyToOne
    @JoinColumn(name = "customerID")
    private Users customer;

    private String customerName;
    private String phoneNumber;
    private String status;

    private Double totalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderID")
    private DeliveryInfo deliveryInfo;

    public String checkOrderStatus(){
        if(!Objects.equals(this.status, "PENDING") && !Objects.equals(this.status, "REJECTED") && !Objects.equals(this.status, "APPROVED")){
            return "Wrong input of Status";
        }
        else return this.status;
    }
    public void changeRejectOrder(){
        this.status = "REJECTED";
    }

    public void changeApproveOrder(){
        this.status = "APPROVED";
    }


}