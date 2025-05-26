package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.Role;
import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.RoleRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.service.email.EmailService;
import com.pinyincentre.pinyin.service.utils.RandomStringGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository roleRepository;

    private static final int PAGE_SIZE = 10;


    @PreAuthorize("hasAuthority('CREATE_ACCOUNT_STUDENT')")
    @Transactional
    @Override
    public UserResponse createUser(UserRequest request) throws IOException {
        String testPass = "12345678";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String passwordGenerate;
        boolean existUsername = false;
        boolean existEmail = false;
        String message;
        User user;
        if(userRepository.existsByUsername(request.getUsername())) {
            existUsername = true;
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            existEmail = true;
        }
        if(!existUsername && !existEmail) {
            user = userMapper.toUser(request);
            user.setStatus(UserStatus.ACTIVE.getCode());

            // set role
            Role role = roleRepository.findById("CENTRE_OWNER")
                    .orElseThrow(() -> new RuntimeException("Role 'STUDENT' not found in DB"));

            user.setRoles(Set.of(role));

            // hash password
            //passwordGenerate = RandomStringGenerator.generate();
            passwordGenerate = testPass;
            user.setPassword(passwordEncoder.encode(passwordGenerate));
            emailService.emailVerification(user.getEmail(), user);
//            message = ErrorCode.CREATE_USER.getMessage();
//            log.warn(message);
        } else {
            if(existEmail) {
                message = ErrorCode.EXIST_EMAIL.getMessage();
                log.warn(message);
            } else {
                message = ErrorCode.EXIST_USERNAME.getMessage();
                log.warn(message);
            }
            return null;
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("@securityUtil.isCurrentUserId(#uid)")
    @Transactional
    @Override
    public UserResponse updateUser(UserUpdateRequest request, String uid) {
        log.info("User request: {}", request);
        User user = userRepository.findById(uid).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        log.info("User information: {},  {}", user, user.getDob());
        userMapper.updateUserFromRequest(request, user);
        user.setUpdatedDate(LocalDateTime.now());

//        var roles = roleRepository.findAllById(request.getRoles());
//        user.setRoles(new HashSet<>(roles));
        log.info("User updated: {}", user);
        User savedUser = userRepository.save(user);
        log.info("User after update: {}", savedUser);
        return userMapper.toUserResponse(savedUser);
    }

    @PreAuthorize("hasAuthority('BAN_USER')")
    @Transactional
    @Override
    public String banUser(String uid) {
        String message;
        int count;
        if(uid.isEmpty()) {
            return ErrorCode.USER_ID_EMPTY.getMessage();
        }
        count = userRepository.changeStatusUserById(uid, UserStatus.BAN.getCode());
        log.info("Number of count change the row: {}", count);
        if(count > 0) {
            message = ErrorCode.BAN_USER_SUCCESS.getMessage();
        } else {
            message = ErrorCode.BAN_USER_FAIL.getMessage();
        }
        return message;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponseProjection> getAllUsersActive(Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        log.info("Current page: {}, page size: {}", currentPage, pageSize);
        int offset = (currentPage - 1) * pageSize;

        return userRepository.listUserPagination(UserStatus.ACTIVE.getCode(), pageSize, offset);
    }

    @PreAuthorize("@securityUtil.isCurrentUserId(#uid)")
    @Override
    public UserResponse getUserById(String uid) {
        User user = userRepository.findById(uid).orElseThrow(
          () -> new AppException(ErrorCode.NOT_FOUND)
        );
        return userMapper.toUserResponse(user);
    }


}
