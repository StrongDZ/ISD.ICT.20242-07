package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.model.DeliveryInfo;

@Mapper(componentModel = "spring")
public interface DeliveryInfoMapper {
    DeliveryInfo toEntity(DeliveryInfo deliveryInfo);

    DeliveryInfoDTO toDto(DeliveryInfo deliveryInfo);
}
