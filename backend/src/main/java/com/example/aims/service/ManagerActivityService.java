package com.example.aims.service;

import com.example.aims.model.ManagerActivity;
import com.example.aims.repository.ManagerActivityRepository;
import com.example.aims.exception.ManagerDailyLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerActivityService {
    
    private final ManagerActivityRepository managerActivityRepository;
    private static final int DAILY_UPDATE_LIMIT = 30;
    private static final int DAILY_DELETE_LIMIT = 30;
    
    @Transactional
    public void checkAndIncrementUpdateCount(int increment) {
        LocalDate today = LocalDate.now();
        Integer currentUpdateCount = managerActivityRepository.getUpdateCountForDate(today);
        if (currentUpdateCount == null) currentUpdateCount = 0;
        if (currentUpdateCount + increment > DAILY_UPDATE_LIMIT) {
            throw new ManagerDailyLimitException(
                String.format("Daily update limit of %d products would be exceeded. Current: %d, Attempting: %d", 
                    DAILY_UPDATE_LIMIT, currentUpdateCount, increment));
        }
        System.out.println("Current update count: " + currentUpdateCount);
        ManagerActivity activity = managerActivityRepository
            .findByActivityDate(today)
            .orElse(new ManagerActivity(null, today, 0, 0));
        System.out.println("Activity: " + activity);
        activity.setUpdateCount(activity.getUpdateCount() + increment);
        managerActivityRepository.save(activity);
        
        log.info("Products updated. Daily update count: {}/{} (increment: {})", 
                activity.getUpdateCount(), DAILY_UPDATE_LIMIT, increment);
    }
    
    @Transactional
    public void checkAndIncrementDeleteCount(int increment) {
        LocalDate today = LocalDate.now();
        Integer currentDeleteCount = managerActivityRepository.getDeleteCountForDate(today);
        if (currentDeleteCount == null) currentDeleteCount = 0;
        if (currentDeleteCount + increment > DAILY_DELETE_LIMIT) {
            throw new ManagerDailyLimitException(
                String.format("Daily delete limit of %d products would be exceeded. Current: %d, Attempting: %d", 
                    DAILY_DELETE_LIMIT, currentDeleteCount, increment));
        }
        
        ManagerActivity activity = managerActivityRepository
            .findByActivityDate(today)
            .orElse(new ManagerActivity(null, today, 0, 0));
        
        activity.setDeleteCount(activity.getDeleteCount() + increment);
        managerActivityRepository.save(activity);
        
        log.info("Products deleted. Daily delete count: {}/{} (increment: {})", 
                activity.getDeleteCount(), DAILY_DELETE_LIMIT, increment);
    }
    
    public Integer getDailyUpdateCount() {
        LocalDate today = LocalDate.now();
        return managerActivityRepository.getUpdateCountForDate(today);
    }
    
    public Integer getDailyDeleteCount() {
        LocalDate today = LocalDate.now();
        return managerActivityRepository.getDeleteCountForDate(today);
    }
} 