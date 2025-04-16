package com.example.aims.repository;

import com.example.aims.model.DVD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DVDRepository extends JpaRepository<DVD, String> {
    List<DVD> findByGenre(String genre);
    List<DVD> findByDirectorContainingIgnoreCase(String director);
}