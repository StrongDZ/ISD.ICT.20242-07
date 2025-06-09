package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CD")
@PrimaryKeyJoinColumn(name = "productID")
public class CD extends Product {

    private String tracklist;
    private String artist;

    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    private String recordLabel;
    private String musicType;
}