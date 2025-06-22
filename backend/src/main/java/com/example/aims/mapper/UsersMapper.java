package com.example.aims.mapper;


import org.mapstruct.Mapper;

import com.example.aims.dto.UsersDTO;
import com.example.aims.model.Users;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UsersDTO toDto(Users user);

    Users toEntity(UsersDTO userDto);
} 
