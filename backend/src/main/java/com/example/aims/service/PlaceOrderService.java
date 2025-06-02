package com.example.aims.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.Product;
import com.example.aims.model.ProductOrderEntity;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.ProductOrderRepository;
import com.example.aims.repository.ProductRepository;

@Service
public class PlaceOrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private final ProductOrderRepository productOrderRepository;


    public PlaceOrderService(OrderRepository orderRepository, ProductRepository productRepository,
                             DeliveryInfoRepository deliveryInfoRepository, ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    
}
