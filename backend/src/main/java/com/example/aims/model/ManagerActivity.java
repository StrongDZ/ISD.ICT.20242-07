package com.example.aims.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "manager_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "activity_date", nullable = false, unique = true)
    private LocalDate activityDate;
    
    @Column(name = "update_count", nullable = false)
    private Integer updateCount = 0;
    
    @Column(name = "delete_count", nullable = false)
    private Integer deleteCount = 0;

    
} 