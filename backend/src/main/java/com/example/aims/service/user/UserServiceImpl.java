package com.example.aims.service.user;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CartItemRepository cartItemRepository;

    @Override
    public List<UserResponse> findAll(String keyword, String sort, int page, int size) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?):(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(2).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Users> userEntities = userRepository.findAll(pageable);

        return userEntities.stream().map(
                (Users entity) -> UserResponse.builder()
                        .id(entity.getId())
                        .userName(entity.getUsername())
                        .gmail(entity.getGmail())
                        .userStatus(entity.getUserStatus() != null ? entity.getUserStatus().name() : null)
                        .userType(entity.getType() != null ? entity.getType().name() : null)
                        .build()
        ).toList();
    }

    public UserResponse findById(Integer id) {
        Users userEntity = getUserEntityById(id);
        return UserResponse.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUsername())
                .gmail(userEntity.getGmail())
                .userStatus(userEntity.getUserStatus() != null ? userEntity.getUserStatus().name() : null)
                .userType(userEntity.getType() != null ? userEntity.getType().name() : null)
                .build();
    }

    public UserResponse findByUsername(String userName) {
        return null;
    }

    public UserResponse findByEmail(String email) {
        return null;
    }

    public long save(UserCreationRequest req) {
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setGmail(req.getGmail());
        user.setType(req.getType());
        user.setUserStatus(UserStatus.NONE);

        userRepository.save(user);
        return 1;
    }

    @Override
    public void update(UserUpdateRequest req) {
        Users user = getUserEntityById(req.getId());

        user.setUsername(req.getUsername());
        user.setGmail(req.getGmail());

        user.setUserStatus(UserStatus.valueOf(req.getUserStatus()));
        user.setType(UserType.valueOf(req.getType()));

        userRepository.save(user);

        try {
            String subject = "Your account information has been updated";
            String body = "Dear " + req.getUsername() + ",\n\nYour account information has been updated by the administrator.\n\nIf you did not request this change, please contact support.";
            emailService.send(req.getGmail(), subject, body);
        } catch (Exception e) {
            // Log email sending failure but don't stop the update process
            System.err.println("Failed to send email notification to " + req.getGmail() + ": " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void changePassword(UserPasswordRequest req) {
        Users user = getUserEntityById(req.getId());

        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(user);
            try {
                String subject = "Your account password has been changed";
                String body = "Dear " + user.getUsername() + ",\n\nYour account password has been changed by the administrator.\n\nIf you did not request this change, please contact support.";
                emailService.send(user.getGmail(), subject, body);
            } catch (Exception e) {
                // Log email sending failure but don't stop the password change process
                System.err.println("Failed to send password change email to " + user.getGmail() + ": " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Password not match");
        }
    }

    @Override
    @Transactional
    public void block(Integer id) {
        Users user = getUserEntityById(id);
        user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        
        try {
            String subject = "Your account has been blocked";
            String body = "Dear " + user.getUsername() + ",\n\nYour account has been blocked by the administrator.\n\nIf you believe this is an error or have any questions, please contact support.\n\nBest regards,\nAdministration Team";
            emailService.send(user.getGmail(), subject, body);
        } catch (Exception e) {
            // Log email sending failure but don't stop the blocking process
            System.err.println("Failed to send account blocking email to " + user.getGmail() + ": " + e.getMessage());
        }
    }

    private Users getUserEntityById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Users user = getUserEntityById(id);
        String userEmail = user.getGmail();
        String userName = user.getUsername();

        cartItemRepository.deleteByCustomer(user);

        userRepository.delete(user);
        try {
            String subject = "Your account has been deleted";
            String body = "Dear " + userName + ",\n\nYour account has been deleted by the administrator.\n\nIf you have any questions, please contact support.";
            emailService.send(userEmail, subject, body);
        } catch (Exception e) {
            // Log email sending failure but don't stop the deletion process
            System.err.println("Failed to send account deletion email to " + userEmail + ": " + e.getMessage());
        }
    }
}
