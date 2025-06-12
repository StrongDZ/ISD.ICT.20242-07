package com.example.aims.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.dto.admin.request.UserCreationRequest;
import com.example.aims.dto.admin.request.UserPasswordRequest;
import com.example.aims.dto.admin.request.UserUpdateRequest;
import com.example.aims.dto.admin.response.UserResponse;

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
    void block(Integer id);
    void delete(Integer id);
}
