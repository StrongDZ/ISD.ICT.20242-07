package com.example.aims.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Data
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    @ManyToOne
    @JoinColumn(name = "customerID")
    private Users customer;

    private String customerName;
    private String phoneNumber;
    private String status;


    private String shippingAddress;
    private String province;
    private Double totalAmount;
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
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
    public Order(String string, Users customerTest, String string2, String string3, String string4, String string5,
            String string6, double d) {
        this.orderID = Integer.parseInt(string);
        this.customer = customerTest;
        this.customerName = string2;
        this.phoneNumber = string3;
        this.status = string4;
        this.shippingAddress = string5;
        this.province = string6;
        this.totalAmount = d;
            }

}