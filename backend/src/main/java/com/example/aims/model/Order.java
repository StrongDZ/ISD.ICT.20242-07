package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    private String status;


    private String shippingAddress;
    private String province;
    private Double totalAmount;

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