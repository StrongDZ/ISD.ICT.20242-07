package com.example.aims.dto.admin.request;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class UserPasswordRequest implements Serializable {
    private Integer id;
    private String password;
    private String confirmPassword;
}
