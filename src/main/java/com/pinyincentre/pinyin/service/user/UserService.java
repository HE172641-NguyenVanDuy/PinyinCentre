package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(UserUpdateRequest request, String uid);

    String banUser(String uid);

    List<UserResponse> getAllUsersActive(Integer pageSize, int currentPage);

    UserResponse getUserById(String uid);

    //List<UserResponse> getAllUsersNotActive();


}
