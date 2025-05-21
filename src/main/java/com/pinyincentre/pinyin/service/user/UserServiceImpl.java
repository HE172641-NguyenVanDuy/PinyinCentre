package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private static final int PAGE_SIZE = 10;

    @Override
    public UserResponse createUser(UserRequest request) {
        boolean existUsername = false;
        boolean existEmail = false;
        String message;
        User user = new User();
        if(!userRepository.existsByUsername(request.getUsername())) {
            existUsername = true;
        }
        if(!userRepository.existsByEmail(request.getEmail())) {
            existEmail = true;
        }
        if(!existUsername && !existEmail) {
            user = userMapper.toUser(request);
            user.setStatus(UserStatus.ACTIVE.getCode());
            message = ErrorCode.CREATE_USER.getMessage();
            log.warn(message);
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

    @Override
    public UserResponse updateUser(UserUpdateRequest request, String uid) {
        User user = userRepository.findById(uid).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        log.info("User information: {}", user);
        userMapper.updateUserFromRequest(request, user);

        user = userRepository.save(user);
        log.info("User after update: {}", user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public String banUser(String uid) {
        String message = "";
        int count = 0;
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

    @Override
    public List<UserResponse> getAllUsersActive(Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        log.info("Current page: {}, page size: {}", currentPage, pageSize);
        int offset = (currentPage - 1) * pageSize;

        return userRepository.listUserPagination(UserStatus.ACTIVE.getCode(), pageSize, offset);
    }

    @Override
    public UserResponse getUserById(String uid) {
        User user = userRepository.findById(uid).orElseThrow(
          () -> new AppException(ErrorCode.NOT_FOUND)
        );
        return userMapper.toUserResponse(user);
    }


}
