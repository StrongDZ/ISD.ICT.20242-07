package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "customerID")
    private Users customer;
    private String customerName;
    private String phoneNumber;
    private String state;

    public Order(String id, Users customer, String customerName, String phoneNumber, String state, String shippingAddress, String province, Double totalAmount) {
        this.id = id;
        this.customer = customer;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.state = state;
        this.shippingAddress = shippingAddress;
        this.province = province;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }
    public String getShippingAddress() {
        return shippingAddress;
    }

    private String shippingAddress;
    private String province;
    private Double totalAmount;
    public Double getTotalAmount() {
        return totalAmount;
    }
    public String checkOrderStatus(){
        if(!Objects.equals(this.state, "PENDING") || !Objects.equals(this.state, "REJECT") || !Objects.equals(this.state, "APPROVE")){
           return "Wrong input if Status";
        }
        else return this.state;
    }

    public void changeRejectOrder(){
        this.state = "REJECT";
    }

    public void changeApproveOrder(){
        this.state = "APPROVE";
    }
}