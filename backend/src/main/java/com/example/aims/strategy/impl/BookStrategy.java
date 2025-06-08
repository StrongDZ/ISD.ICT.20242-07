package com.example.aims.strategy.impl;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.mapper.BookMapper;
import com.example.aims.model.Book;
import com.example.aims.repository.BookRepository;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.util.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookStrategy implements ProductStrategy {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookStrategy(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        BookDTO bookDTO = (BookDTO) productDTO;

        // Create Book entity (includes Product fields)
        Book book = bookMapper.toEntity(bookDTO);
        if (book.getProductID() == null) {
            book.setProductID(IdGenerator.generateProductId("book"));
        }
        book.setCategory("book");
        Book savedBook = bookRepository.save(book);

        // Return DTO
        return bookMapper.toDTO(savedBook);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        BookDTO bookDTO = (BookDTO) productDTO;

        // Check if book exists
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", "id", id);
        }

        // Update Book
        Book book = bookMapper.toEntity(bookDTO);
        book.setProductID(id);
        book.setCategory("book");
        Book savedBook = bookRepository.save(book);

        // Return DTO
        return bookMapper.toDTO(savedBook);
    }

    @Override
    public void deleteProduct(String id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", "id", id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        return bookMapper.toDTO(book);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return bookRepository.findAll().stream()
                .map(book -> (ProductDTO) bookMapper.toDTO(book))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle() != null &&
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .map(book -> (ProductDTO) bookMapper.toDTO(book))
                .collect(Collectors.toList());
    }

    @Override
    public String getProductType() {
        return "book";
    }
}