package com.example.aims.dto.products;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DvdDTO extends ProductDTO {
    private String discType;
    private String runtime;
    private String studio;
    private String director;
    private String subtitle;
    private Date releaseDate;
    private String language;
    private String genre;

    // Manual getter and setter for eligible to ensure compatibility
    @Override
    public Boolean getEligible() {
        return super.getEligible();
    }

    @Override
    public void setEligible(Boolean eligible) {
        super.setEligible(eligible);
    }
}
