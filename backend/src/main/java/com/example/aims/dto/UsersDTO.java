package com.example.aims.dto;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {

    private Integer id;
    private String username;
    private String password;
    private String gmail;

    private UserType type;

    private UserStatus userStatus;
}