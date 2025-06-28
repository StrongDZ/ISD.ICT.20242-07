package com.example.aims.dto.products;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CdDTO extends ProductDTO {
    private String tracklist;
    private String artist;
    private Date releaseDate;
    private String recordLabel;
    private String musicType;

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
