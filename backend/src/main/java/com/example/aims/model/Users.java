package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;

@Data
@Getter
@Setter
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public class Users implements  UserDetails, Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name ="username", unique = true, nullable = false, length = 255)
    private String username;
    @Column(name ="password", unique = true, nullable = false, length = 255)
    private String password;
    @Column(name = "gmail", unique = true, nullable = false, length = 15)
    private String gmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "userstatus")
    private UserStatus userStatus;

    public Users(int i, String string, String string2, String string3) {
        this.id = i;
        this.gmail = string;
        this.username = string2;
        this.password = string3;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of();
    }
    @Override
    public boolean isEnabled() {
        return UserStatus.NONE.equals(userStatus);
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAccountNonExpired'");
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAccountNonLocked'");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCredentialsNonExpired'");
    }
    // @Override
    // public boolean isAccountNonExpired() {
    //     return UserDetails.super.isAccountNonExpired();
    // }
    // @Override
    // public boolean isAccountNonLocked() {
    //     return UserDetails.super.isAccountNonLocked();
    // }
    // @Override
    // public boolean isCredentialsNonExpired() {
    //     return UserDetails.super.isCredentialsNonExpired();
    // }


}