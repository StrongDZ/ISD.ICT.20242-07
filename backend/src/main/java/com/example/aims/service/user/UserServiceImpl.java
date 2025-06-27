package com.example.aims.service.user;

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
import com.example.aims.dto.admin.request.UserCreationRequest;
import com.example.aims.dto.admin.request.UserPasswordRequest;
import com.example.aims.dto.admin.request.UserUpdateRequest;
import com.example.aims.dto.admin.response.UserResponse;
import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
                .userStatus(entity.getUserStatus() != null ? entity.getUserStatus().name() : null)
                .userType(entity.getType() != null ? entity.getType().name() : null)
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
                .userStatus(userEntity.getUserStatus() != null ? userEntity.getUserStatus().name() : null)
                .userType(userEntity.getType() != null ? userEntity.getType().name() : null)
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
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
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

        // Gửi email thông báo cập nhật thông tin
        try {
            String subject = "Your account information has been updated";
            String body = "Dear " + req.getUsername() + ",\n\nYour account information has been updated by the administrator.\n\nIf you did not request this change, please contact support.";
            emailService.send(req.getGmail(), subject, body);
            log.info("Sent update notification email to {}", req.getGmail());
        } catch (Exception e) {
            log.error("Failed to send update notification email: {}", e.getMessage());
        }

    }
    @Override
    @Transactional
    public void changePassword(UserPasswordRequest req){
        log.info("Change password for user: {}", req);
        Users user = getUserEntityById(req.getId());
        
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(user);
            log.info("Password updated for user: {}", user.getUsername());
            // Gửi email thông báo đổi mật khẩu
            try {
                String subject = "Your account password has been changed";
                String body = "Dear " + user.getUsername() + ",\n\nYour account password has been changed by the administrator.\n\nIf you did not request this change, please contact support.";
                emailService.send(user.getGmail(), subject, body);
                log.info("Sent password change notification email to {}", user.getGmail());
            } catch (Exception e) {
                log.error("Failed to send password change notification email: {}", e.getMessage());
            }
        } else {
            log.info("Compare password failed");
            throw new RuntimeException("Password not match");
        }
    }


    @Override
    @Transactional
    /**
     * Block user by id
     * @param id
     */
    public void block(Integer id){
        log.info("Deleting user: {}", id);

        // Get user by id
        Users user = getUserEntityById(id);
       user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        log.info("Blocked user id: {}", id);
       return;
    }
    private Users getUserEntityById(Integer id){
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Users user = getUserEntityById(id);
        String userEmail = user.getGmail();
        String userName = user.getUsername();
        userRepository.delete(user);
        log.info("Hard deleted user id: {}", id);
        // Gửi email thông báo xóa tài khoản
        try {
            String subject = "Your account has been deleted";
            String body = "Dear " + userName + ",\n\nYour account has been deleted by the administrator.\n\nIf you have any questions, please contact support.";
            emailService.send(userEmail, subject, body);
            log.info("Sent account deletion notification email to {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send account deletion notification email: {}", e.getMessage());
        }
    }

}
