package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public interface UserService {

    UserResponse createUser(UserRequest request) throws IOException;

    UserResponse updateUser(UserUpdateRequest request, String uid);

    String banUser(String uid);

    List<UserResponseProjection> getAllUsersActive(Integer pageSize, int currentPage);

    UserResponse getUserById(String uid);

    List<UserResponseProjection> getUserByRole(String role, Integer pageSize, int currentPage);

    UserResponse createTeacherAccount(UserRequest request) throws IOException;
    //List<UserResponse> getAllUsersNotActive();

    Set<String> getRoleNames(String username);

    UserEntity getUserByUserName(String userName);

}
