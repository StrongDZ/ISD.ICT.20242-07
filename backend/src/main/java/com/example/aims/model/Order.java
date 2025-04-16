package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
    
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "customerID")
    private Users customer;
    
    private String status;
}