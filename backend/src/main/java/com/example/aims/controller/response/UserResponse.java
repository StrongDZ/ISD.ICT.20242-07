package com.example.aims.controller.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private int id;
    private String userName;
    private String gmail;

    public void setID(int l) {
        this.id = l;
    }

    public void setEmail(String mail) {
        this.gmail = mail;
    }
}
