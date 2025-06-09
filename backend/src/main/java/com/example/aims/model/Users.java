package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;

@Data
@Getter
@Setter
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
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

    public Users(int i, String string, String string2, String string3) {
        this.id = i;
        this.gmail = string;
        this.username = string2;
        this.password = string3;
    }

}