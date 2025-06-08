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

}
