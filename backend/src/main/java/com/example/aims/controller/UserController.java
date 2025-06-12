package com.example.aims.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.example.aims.dto.admin.request.UserCreationRequest;
import com.example.aims.dto.admin.request.UserPasswordRequest;
import com.example.aims.dto.admin.request.UserUpdateRequest;
import com.example.aims.dto.admin.response.UserResponse;
import com.example.aims.service.user.UserService;
import com.example.aims.service.user.EmailService;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
     private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
         this.emailService = emailService;
    }

    @Operation(summary = "Get User List")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get user list");

        List<UserResponse> userList = userService.findAll(keyword, "id", page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User List");
        result.put("data", userList);
        return result;
    }

    @Operation(summary = "Get User Detail")
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getUserDetail(@PathVariable Long userId) {
        // UserResponse userDetail = new UserResponse();
        // userDetail.setID(1);
        // userDetail.setUserName("admin");
        UserResponse userDetail = userService.findById(userId.intValue());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user");
        result.put("data", userDetail);
        return result;
    }

    @Operation(summary = "Create User")
    @PostMapping("/add")
    public ResponseEntity<Long> createUser(@RequestBody UserCreationRequest request) {

        // Map<String, Object> result = new LinkedHashMap<>();
        // result.put("status", HttpStatus.CREATED.value());
        // result.put("message", "user created sucessfully");
        // result.put("data", 3);
        // return result;
        userService.save(request);
        return new ResponseEntity<>(1l, HttpStatus.CREATED);
    }

    @Operation(summary = "Update User")
    @PutMapping("/upd")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Map<String, Object> updateUser(@RequestBody UserUpdateRequest request) {
        userService.update(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "user updated successfully");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Change User Password")
    @PatchMapping("/change_pwd")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Map<String, Object> changePassword(@RequestBody UserPasswordRequest request) {
        userService.changePassword(request);
        log.info("Change password for user: {}", request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "password updated sucessfully");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Block User") // xóa mềm
    @DeleteMapping("/block/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> blockUser(@PathVariable Integer userId) {
        userService.block(userId);
        log.info("Block user with ID: {}", userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "block user sucessfully");
        result.put("data", "");
        return result;
    }

    @Operation(summary = "Delete User (Hard Delete)")
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> deleteUser(@PathVariable Integer userId) {
        userService.delete(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User deleted successfully");
        result.put("data", "");
        return result;
    }

}