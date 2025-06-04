package com.example.aims.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;


@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name ="user_name", unique = true, nullable = false, length = 255)
    private String username;
    @Column(name ="password", unique = true, nullable = false, length = 255)
    private String password;
    @Column(name = "gmail", unique = true, nullable = false, length = 15)
    private String gmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

}