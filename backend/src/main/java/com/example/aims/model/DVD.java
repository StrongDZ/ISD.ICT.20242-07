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
@Table(name = "DVD")
@PrimaryKeyJoinColumn(name = "productID")
public class DVD extends Product {

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