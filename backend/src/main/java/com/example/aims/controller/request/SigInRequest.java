package com.example.aims.controller.request;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.ToString;

@Service
@Getter
@ToString
public class SigInRequest implements Serializable {
    private String username;
    private String password;
    

}
