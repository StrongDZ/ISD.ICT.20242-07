package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;

@Data
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
    private Integer id;
    @Column(name ="user_name", unique = true, nullable = false, length = 255)
    private String username;
    @Column(name ="password", unique = true, nullable = false, length = 255)
    private String password;
    @Column(name = "gmail", unique = true, nullable = false, length = 255)
    private String gmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;

    public Users(Integer id, String gmail, String username, String password) {
        this.id = id;
        this.gmail = gmail;
        this.username = username;
        this.password = password;
    }

}