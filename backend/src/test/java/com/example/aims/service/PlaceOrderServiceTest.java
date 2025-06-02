package com.example.aims.service;

import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaceOrderServiceTest {

    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DeliveryInfoRepository deliveryInfoRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
