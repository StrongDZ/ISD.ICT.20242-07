package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class Users {

    @Id
    private String id;
    
    private String role;
    
    @Column(unique = true)
    private String username;
    
    private String password;
}