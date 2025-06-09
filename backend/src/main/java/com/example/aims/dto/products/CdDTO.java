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

}
