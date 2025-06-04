package com.example.aims.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.aims.common.UserStatus;
import com.example.aims.common.UserType;
import com.example.aims.controller.request.UserPasswordRequest;
import com.example.aims.controller.request.UserUpdateRequest;
import com.example.aims.controller.response.UserResponse;
import com.example.aims.model.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> findAll(String keyword, String sort, int page, int size) {
        // if (StringUtils.hasLength(keyword)) {
        //     // goi search method

        // Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\w+?):(.*)"); // tencot:asc|desc
            java.util.regex.Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(2).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }
    
        // Paging
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Users> userEntities = userRepository.findAll(pageable);

        List<UserResponse> userList = userEntities.stream().map(
            (Users entity) -> UserResponse.builder()
               
                .id(entity.getId())
                .userName(entity.getUsername())
                .gmail(entity.getGmail())
                .build()
        ).toList();

        return userList;
       
    }
    public UserResponse findById(Integer id){
        Users userEntity =  getUserEntityById(id);
        return UserResponse.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUsername())
                .gmail(userEntity.getGmail())
                .build();
    }
    public UserResponse findByUsername(String userName){
        return null;
    }
    public UserResponse findByEmail(String email){
        return null;
    }
    public long save(UserCreationRequest req){
        log.info("Saving user {}", req);
        Users user = new Users();
        user.setUsername(req.getUserName());
        user.setPassword(req.getPassword());
        user.setGmail(req.getGmail());
        user.setType(req.getType());
        user.setUserStatus(UserStatus.NONE);

        userRepository.save(user);
        log.info("Received gmail: {}", req.getGmail());
        log.info("Saved user {}", user);
        return 1;
    }
    @Override
    public void update(UserUpdateRequest req){
        //get user by ID;

        log.info("Updating User: {}", req);
        Users user = getUserEntityById(req.getId());

        user.setUsername(req.getUsername());
       // user.setPassword(req.getPassword());
        user.setGmail(req.getGmail());
        
        user.setUserStatus(UserStatus.valueOf(req.getUserStatus()));
        user.setType(UserType.valueOf(req.getType()));

        userRepository.save(user);
        log.info("Updated user {}", user);


    }
    @Override
    @Transactional
    public void changePassword(UserPasswordRequest req){
        log.info("Change password for user: {}", req);
        Users user = getUserEntityById(req.getId());
        
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        } // Set password trực tiếp không cần encode
        else{
            log.info("Compare password failed");
            //throw new RuntimeException("Password not match");
        }
        userRepository.save(user);
        log.info("After password for user: {}", user.getUsername());
        return;
    }
    @Override
    @Transactional
    /**
     * Delete user by id
     * @param id
     */
    public void delete(Integer id){
        log.info("Deleting user: {}", id);

        // Get user by id
        Users user = getUserEntityById(id);
       user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        log.info("Deleted user id: {}", id);
       return;
    }
    private Users getUserEntityById(Integer id){
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
    }
}
