package com.example.aims.controller.request;

import java.io.Serializable;

import org.hibernate.usertype.UserType;

import com.example.aims.common.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationRequest implements Serializable {

    private String userName;
    private String password;
    private String gmail;
    private UserType type;
    private UserStatus status;


}
