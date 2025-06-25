package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper extends ProductMapper<Book, BookDTO> {

    @Override
    BookDTO toDTO(Book book);

    @Override
    Book toEntity(BookDTO bookDTO);

    @Override
    default String getProductType() {
        return "book";      
    }
}
