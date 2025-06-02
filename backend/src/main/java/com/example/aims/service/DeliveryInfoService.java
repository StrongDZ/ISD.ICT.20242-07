package com.example.aims.service;

import org.springframework.stereotype.Service;

import com.example.aims.model.DeliveryInfo;
import com.example.aims.repository.DeliveryInfoRepository;

@Service
public class DeliveryInfoService {

    private final DeliveryInfoRepository deliveryInfoRepository;

    public DeliveryInfoService(DeliveryInfoRepository deliveryInfoRepository) {
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    public DeliveryInfo createDeliveryInfo(DeliveryInfo deliveryInfo) {
        return deliveryInfoRepository.save(deliveryInfo);
    }
    
}
