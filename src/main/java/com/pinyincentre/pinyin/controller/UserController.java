package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get-users-active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsersActive(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<UserResponse> lstUsers = userService.getAllUsersActive(pageSize, page);
        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(lstUsers)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id) {
        UserResponse user = userService.getUserById(id);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<ApiResponse<String>> banUser(@PathVariable("id") String id) {
        String msg = userService.banUser(id);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(msg)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("id") String id,@Valid @RequestBody UserUpdateRequest userRequest) {
        UserResponse userResponse = userService.updateUser(userRequest, id);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(userResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserRequest userRequest) throws IOException {
        UserResponse userResponse = userService.createUser(userRequest);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(userResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
