package com.example.aims.repository;

import com.example.aims.model.ManagerActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ManagerActivityRepository extends JpaRepository<ManagerActivity, Long> {
    
    @Query("SELECT ma FROM ManagerActivity ma WHERE ma.activityDate = :activityDate")
    Optional<ManagerActivity> findByActivityDate(@Param("activityDate") LocalDate activityDate);
    
    @Query("SELECT COALESCE(ma.updateCount, 0) FROM ManagerActivity ma WHERE ma.activityDate = :activityDate")
    Integer getUpdateCountForDate(@Param("activityDate") LocalDate activityDate);
    
    @Query("SELECT COALESCE(ma.deleteCount, 0) FROM ManagerActivity ma WHERE ma.activityDate = :activityDate")
    Integer getDeleteCountForDate(@Param("activityDate") LocalDate activityDate);
} 