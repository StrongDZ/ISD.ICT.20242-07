package com.example.aims.repository;

import com.example.aims.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByGenre(String genre);
    List<Book> findByAuthorsContainingIgnoreCase(String author);
    List<Book> findByPublisher(String publisher);
}