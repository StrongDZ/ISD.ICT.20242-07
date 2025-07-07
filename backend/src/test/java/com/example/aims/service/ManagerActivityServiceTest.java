package com.example.aims.service;

import com.example.aims.model.ManagerActivity;
import com.example.aims.repository.ManagerActivityRepository;
import com.example.aims.exception.ManagerDailyLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerActivityServiceTest {

    @Mock
    private ManagerActivityRepository managerActivityRepository;

    @InjectMocks
    private ManagerActivityService managerActivityService;

    private Integer managerId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        managerId = 1;
        today = LocalDate.now();
    }

    @Test
    void testCheckAndIncrementUpdateCount_FirstUpdate() {
        // Given
        when(managerActivityRepository.getUpdateCountForDate(today))
                .thenReturn(0);
        when(managerActivityRepository.findByActivityDate(today))
                .thenReturn(Optional.empty());
        when(managerActivityRepository.save(any(ManagerActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        assertDoesNotThrow(() -> managerActivityService.checkAndIncrementUpdateCount(1));

        // Then
        verify(managerActivityRepository).save(any(ManagerActivity.class));
    }

    @Test
    void testCheckAndIncrementUpdateCount_ReachLimit() {
        // Given
        when(managerActivityRepository.getUpdateCountForDate(today))
                .thenReturn(30);

        // When & Then
        ManagerDailyLimitException exception = assertThrows(
                ManagerDailyLimitException.class,
                () -> managerActivityService.checkAndIncrementUpdateCount(1)
        );
        assertTrue(exception.getMessage().contains("Daily update limit"));
    }

    @Test
    void testCheckAndIncrementUpdateCount_WithIncrement() {
        // Given
        when(managerActivityRepository.getUpdateCountForDate(today))
                .thenReturn(25);
        when(managerActivityRepository.findByActivityDate(today))
                .thenReturn(Optional.of(new ManagerActivity(1L, today, 25, 0)));
        when(managerActivityRepository.save(any(ManagerActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        assertDoesNotThrow(() -> managerActivityService.checkAndIncrementUpdateCount(5));

        // Then
        verify(managerActivityRepository).save(any(ManagerActivity.class));
    }

    @Test
    void testCheckAndIncrementUpdateCount_ExceedLimitWithIncrement() {
        // Given
        when(managerActivityRepository.getUpdateCountForDate(today))
                .thenReturn(28);

        // When & Then
        ManagerDailyLimitException exception = assertThrows(
                ManagerDailyLimitException.class,
                () -> managerActivityService.checkAndIncrementUpdateCount(5)
        );
        assertTrue(exception.getMessage().contains("would be exceeded"));
        assertTrue(exception.getMessage().contains("Current: 28, Attempting: 5"));
    }

    @Test
    void testCheckAndIncrementDeleteCount_FirstDelete() {
        // Given
        when(managerActivityRepository.getDeleteCountForDate(today))
                .thenReturn(0);
        when(managerActivityRepository.findByActivityDate(today))
                .thenReturn(Optional.empty());
        when(managerActivityRepository.save(any(ManagerActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        assertDoesNotThrow(() -> managerActivityService.checkAndIncrementDeleteCount(1));

        // Then
        verify(managerActivityRepository).save(any(ManagerActivity.class));
    }

    @Test
    void testCheckAndIncrementDeleteCount_ReachLimit() {
        // Given
        when(managerActivityRepository.getDeleteCountForDate(today))
                .thenReturn(30);

        // When & Then
        ManagerDailyLimitException exception = assertThrows(
                ManagerDailyLimitException.class,
                () -> managerActivityService.checkAndIncrementDeleteCount(1)
        );
        assertTrue(exception.getMessage().contains("Daily delete limit"));
    }

    @Test
    void testCheckAndIncrementDeleteCount_WithIncrement() {
        // Given
        when(managerActivityRepository.getDeleteCountForDate(today))
                .thenReturn(20);
        when(managerActivityRepository.findByActivityDate(today))
                .thenReturn(Optional.of(new ManagerActivity(1L, today, 0, 20)));
        when(managerActivityRepository.save(any(ManagerActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        assertDoesNotThrow(() -> managerActivityService.checkAndIncrementDeleteCount(10));

        // Then
        verify(managerActivityRepository).save(any(ManagerActivity.class));
    }

    @Test
    void testCheckAndIncrementDeleteCount_ExceedLimitWithIncrement() {
        // Given
        when(managerActivityRepository.getDeleteCountForDate(today))
                .thenReturn(25);

        // When & Then
        ManagerDailyLimitException exception = assertThrows(
                ManagerDailyLimitException.class,
                () -> managerActivityService.checkAndIncrementDeleteCount(10)
        );
        assertTrue(exception.getMessage().contains("would be exceeded"));
        assertTrue(exception.getMessage().contains("Current: 25, Attempting: 10"));
    }

    @Test
    void testGetDailyUpdateCount() {
        // Given
        when(managerActivityRepository.getUpdateCountForDate(today))
                .thenReturn(15);

        // When
        Integer count = managerActivityService.getDailyUpdateCount();

        // Then
        assertEquals(15, count);
    }

    @Test
    void testGetDailyDeleteCount() {
        // Given
        when(managerActivityRepository.getDeleteCountForDate(today))
                .thenReturn(10);

        // When
        Integer count = managerActivityService.getDailyDeleteCount();

        // Then
        assertEquals(10, count);
    }
} 