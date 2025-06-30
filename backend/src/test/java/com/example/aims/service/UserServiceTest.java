package com.example.aims.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;
import com.example.aims.dto.admin.request.UserCreationRequest;
import com.example.aims.dto.admin.request.UserPasswordRequest;
import com.example.aims.dto.admin.request.UserUpdateRequest;
import com.example.aims.dto.admin.response.UserResponse;
import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.EmailService;
import com.example.aims.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private Users testUser;
    private UserCreationRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserPasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new Users();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setGmail("test@example.com");
        testUser.setType(UserType.CUSTOMER);
        testUser.setUserStatus(UserStatus.NONE);

        // Setup create request
        createRequest = new UserCreationRequest();
        createRequest.setUsername("newuser");
        createRequest.setPassword("password123");
        createRequest.setGmail("newuser@example.com");
        createRequest.setType(UserType.CUSTOMER);

        // Setup update request
        updateRequest = new UserUpdateRequest();
        updateRequest.setId(1);
        updateRequest.setUsername("updateduser");
        updateRequest.setGmail("updated@example.com");
        updateRequest.setType("CUSTOMER");
        updateRequest.setUserStatus("NONE");

        // Setup password request
        passwordRequest = new UserPasswordRequest();
        passwordRequest.setId(1);
        passwordRequest.setPassword("newpassword");
        passwordRequest.setConfirmPassword("newpassword");
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        String keyword = "test";
        String sort = "username:asc";
        int page = 0;
        int size = 20;

        List<Users> userList = Arrays.asList(testUser);
        Page<Users> userPage = new PageImpl<>(userList);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getUsername(), result.get(0).getUserName());
        assertEquals(testUser.getGmail(), result.get(0).getGmail());
        assertEquals(testUser.getUserStatus().name(), result.get(0).getUserStatus());
        assertEquals(testUser.getType().name(), result.get(0).getUserType());

        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindAll_WithDescSort() {
        // Arrange
        String keyword = "test";
        String sort = "username:desc";
        int page = 0;
        int size = 20;

        List<Users> userList = Arrays.asList(testUser);
        Page<Users> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindAll_WithInvalidSort() {
        // Arrange
        String keyword = "test";
        String sort = "invalid:sort";
        int page = 0;
        int size = 20;

        List<Users> userList = Arrays.asList(testUser);
        Page<Users> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUserName());
        assertEquals(testUser.getGmail(), result.getGmail());
        assertEquals(testUser.getUserStatus().name(), result.getUserStatus());
        assertEquals(testUser.getType().name(), result.getUserType());

        verify(userRepository).findById(userId);
    }

    @Test
    void testFindById_UserNotFound() {
        // Arrange
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.findById(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(userId);
    }


    @Test
    void testSave_Success() throws Exception {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        long result = userService.save(createRequest);

        // Assert
        assertEquals(1, result);
        verify(passwordEncoder).encode(createRequest.getPassword());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void testSave_WithNullStatus() {
        // Arrange
        createRequest.setStatus(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        long result = userService.save(createRequest);

        // Assert
        assertEquals(1, result);
        verify(userRepository).save(argThat(user -> user.getUserStatus() == UserStatus.NONE));
    }

    @Test
    void testUpdate_Success() throws Exception {
        // Arrange
        when(userRepository.findById(updateRequest.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        userService.update(updateRequest);

        // Assert
        verify(userRepository).findById(updateRequest.getId());
        verify(userRepository).save(any(Users.class));
        verify(emailService).send(eq(updateRequest.getGmail()), anyString(), anyString());
    }

    @Test
    void testUpdate_UserNotFound() {
        // Arrange
        when(userRepository.findById(updateRequest.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.update(updateRequest));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(updateRequest.getId());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testUpdate_EmailServiceException() throws Exception {
        // Arrange
        when(userRepository.findById(updateRequest.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);
        doThrow(new RuntimeException("Email service error")).when(emailService).send(anyString(), anyString(), anyString());

        // Act
        userService.update(updateRequest);

        // Assert - Should not throw exception, just log error
        verify(userRepository).findById(updateRequest.getId());
        verify(userRepository).save(any(Users.class));
        verify(emailService).send(eq(updateRequest.getGmail()), anyString(), anyString());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Arrange
        when(userRepository.findById(passwordRequest.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        userService.changePassword(passwordRequest);

        // Assert
        verify(userRepository).findById(passwordRequest.getId());
        verify(passwordEncoder).encode(passwordRequest.getPassword());
        verify(userRepository).save(any(Users.class));
        verify(emailService).send(eq(testUser.getGmail()), anyString(), anyString());
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        // Arrange
        passwordRequest.setConfirmPassword("differentpassword");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.changePassword(passwordRequest));
        assertEquals("Password not match", exception.getMessage());

        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Arrange
        when(userRepository.findById(passwordRequest.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.changePassword(passwordRequest));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(passwordRequest.getId());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testChangePassword_EmailServiceException() throws Exception {
        // Arrange
        when(userRepository.findById(passwordRequest.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);
        doThrow(new RuntimeException("Email service error")).when(emailService).send(anyString(), anyString(), anyString());

        // Act
        userService.changePassword(passwordRequest);

        // Assert - Should not throw exception, just log error
        verify(userRepository).findById(passwordRequest.getId());
        verify(passwordEncoder).encode(passwordRequest.getPassword());
        verify(userRepository).save(any(Users.class));
        verify(emailService).send(eq(testUser.getGmail()), anyString(), anyString());
    }

    @Test
    void testBlock_Success() {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        userService.block(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(argThat(user -> user.getUserStatus() == UserStatus.BLOCKED));
    }

    @Test
    void testBlock_UserNotFound() {
        // Arrange
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.block(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).delete(testUser);
        verify(emailService).send(eq(testUser.getGmail()), anyString(), anyString());
    }

    @Test
    void testDelete_UserNotFound() {
        // Arrange
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.delete(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(Users.class));
    }

    @Test
    void testDelete_EmailServiceException() throws Exception {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Email service error")).when(emailService).send(anyString(), anyString(), anyString());

        // Act
        userService.delete(userId);

        // Assert - Should not throw exception, just log error
        verify(userRepository).findById(userId);
        verify(userRepository).delete(testUser);
        verify(emailService).send(eq(testUser.getGmail()), anyString(), anyString());
    }

    @Test
    void testFindAll_EmptyResult() {
        // Arrange
        String keyword = "nonexistent";
        String sort = "username:asc";
        int page = 0;
        int size = 20;

        Page<Users> emptyPage = new PageImpl<>(Arrays.asList());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindAll_WithNullKeyword() {
        // Arrange
        String keyword = null;
        String sort = "username:asc";
        int page = 0;
        int size = 20;

        List<Users> userList = Arrays.asList(testUser);
        Page<Users> userPage = new PageImpl<>(userList);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindAll_WithNullSort() {
        // Arrange
        String keyword = "test";
        String sort = null;
        int page = 0;
        int size = 20;

        List<Users> userList = Arrays.asList(testUser);
        Page<Users> userPage = new PageImpl<>(userList);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        List<UserResponse> result = userService.findAll(keyword, sort, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindById_WithNullUserStatus() {
        // Arrange
        Integer userId = 1;
        testUser.setUserStatus(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserStatus());
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindById_WithNullUserType() {
        // Arrange
        Integer userId = 1;
        testUser.setType(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserType());
        verify(userRepository).findById(userId);
    }
} 