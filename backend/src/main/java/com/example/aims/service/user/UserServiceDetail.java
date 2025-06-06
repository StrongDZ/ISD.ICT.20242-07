package com.example.aims.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

import com.example.aims.repository.UsersRepository;


@Service
@RequiredArgsConstructor
public class UserServiceDetail {
    private final UsersRepository userRepository;
    public UserDetailsService userService() {
        return userRepository::findByUsername;
    }
   
    
}
