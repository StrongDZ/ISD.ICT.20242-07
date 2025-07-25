package com.example.aims.repository;

import com.example.aims.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsersname(String username);
}