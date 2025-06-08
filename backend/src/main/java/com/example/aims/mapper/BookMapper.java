package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toDTO(Book book);

    Book toEntity(BookDTO bookDTO);
}
