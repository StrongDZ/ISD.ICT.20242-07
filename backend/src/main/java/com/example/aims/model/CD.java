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
@Table(name = "CD")
public class CD {
    
    @Id
    private String productID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "productID")
    private Product product;
    
    private String tracklist;
    private String artist;
    
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    
    private String recordLabel;
    private String musicType;
}