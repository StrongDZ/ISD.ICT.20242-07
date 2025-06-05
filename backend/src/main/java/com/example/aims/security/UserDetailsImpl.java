package com.example.aims.security;

import com.example.aims.model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.aims.common.UserStatus;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String gmail;
    
    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
    private UserStatus userStatus;

    public UserDetailsImpl(String id, String username, String gmail, String password,
                           Collection<? extends GrantedAuthority> authorities, UserStatus userStatus) {
        this.id = id;
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.authorities = authorities;
        this.userStatus = userStatus;
    }

    public static UserDetailsImpl build(Users user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getType().name()));

        return new UserDetailsImpl(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getGmail(),
                user.getPassword(),
                authorities,
                user.getUserStatus());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getGmail() {
        return gmail;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }
} 