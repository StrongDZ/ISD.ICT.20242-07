package com.example.aims.repository;

import com.example.aims.model.CD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CDRepository extends JpaRepository<CD, String> {
    List<CD> findByArtistContainingIgnoreCase(String artist);
    List<CD> findByMusicType(String musicType);
}