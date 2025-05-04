package com.example.aims.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class OrderTest {
    Order testthisOrder;
    Users customerTest;

    @BeforeEach
    void setUp() {
        // Tạo đối tượng Users bằng AllArgsConstructor
        customerTest = new Users("1001", "Customer", "thoconbexinh", "112233");

        // Tạo đối tượng Order (giả sử constructor của Order đã được cập nhật để nhận đối tượng Users)
        testthisOrder = new Order("1", customerTest, "Thùy Dương", "0373629481", "ABCXYZ", "Thị Cầu, Bắc Ninh", "Bắc Ninh", 345.000);
    }

    @Test
    void getId() {
        assertEquals("1", testthisOrder.getId());
    }

    @Test
    void getShippingAddress() {
        assertEquals("Thị Cầu, Bắc Ninh", testthisOrder.getShippingAddress());
    }

    @Test
    void getTotalAmount() {
        assertEquals(345.000, testthisOrder.getTotalAmount());
    }

    @Test
    void checkOrderStatus() {
        testthisOrder = new Order("1", customerTest, "Thùy Dương", "0373629481", "ABCXYZ", "Bắc Ninh", "Thị Cầu, Bắc Ninh", 345.000);
        assertEquals("PENDING", testthisOrder.getStatus());
        assertEquals("Wrong input if Status", testthisOrder.getStatus());
    }

    @Test
    void changeRejectOrder() {
        testthisOrder.setStatus("REJECTED");
        assertEquals("REJECTED", testthisOrder.getStatus()); // Giả sử có phương thức checkOrderStatus
    }

    @Test
    void changeApproveOrder() {
        testthisOrder.setStatus("APPROVED");
        assertEquals("APPROVED", testthisOrder.getStatus()); // Giả sử có phương thức checkOrderStatus
    }
}