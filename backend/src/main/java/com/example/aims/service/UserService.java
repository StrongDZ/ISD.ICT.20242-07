package com.example.aims.service;

import org.springframework.stereotype.Service;

import com.example.aims.controller.request.UserCreationRequest;
import com.example.aims.controller.request.UserPasswordRequest;
import com.example.aims.controller.request.UserUpdateRequest;
import com.example.aims.controller.response.UserResponse;

import java.util.List;
@Service
public interface UserService {
    List<UserResponse> findAll(String keyword, String sort, int page, int size);
    UserResponse findById(Integer id);
    UserResponse findByUsername(String userName);
    UserResponse findByEmail(String email);
    long save(UserCreationRequest req);
    void update(UserUpdateRequest req);
    void changePassword(UserPasswordRequest req);
    void delete(Integer id);

}
