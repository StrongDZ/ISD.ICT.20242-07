package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DVD")
public class DVD {
    
    @Id
    private String productID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "productID")
    private Product product;
    
    private String discType;
    private String runtime;
    private String studio;
    private String director;
    private String subtitle;
    
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    
    private String language;
    private String genre;
}