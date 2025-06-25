package com.example.aims.mapper;

import org.mapstruct.Mapper;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.model.DeliveryInfo;

@Mapper(componentModel = "spring")
public interface DeliveryInfoMapper {
    DeliveryInfo toEntity(DeliveryInfoDTO deliveryInfoDTO);

    DeliveryInfoDTO toDto(DeliveryInfo deliveryInfo);
}
