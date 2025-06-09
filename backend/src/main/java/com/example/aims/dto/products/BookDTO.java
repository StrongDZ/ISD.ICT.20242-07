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

}
 