package com.example.aims.dto.products;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookDTO extends ProductDTO {
    private String coverType;
    private String authors;
    private String publisher;
    private Integer numberOfPages;
    private String language;
    private String genre;
    private Date pubDate;

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
 