package com.example.aims.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.example.aims.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UserServiceDetail {
    private final UserRepository userRepository;
    public UserDetailsService userService() {
        return userRepository::findByUsername;
    }
}
