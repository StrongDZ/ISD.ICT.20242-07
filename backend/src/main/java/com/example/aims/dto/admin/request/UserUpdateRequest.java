package com.example.aims.dto.admin.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class UserUpdateRequest implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String gmail;
    private String type;
    private String userStatus;
}
