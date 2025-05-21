package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.RegistrationInfoRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.RegistrationInfoResponse;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.service.registration_info.RegistrationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration-info")
@Slf4j(topic = "REGISTRATION-CONTROLLER")
public class RegistrationInfoController {

    @Autowired
    private RegistrationInfoService registrationInfoService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RegistrationInfoResponse>> createRegistrationInfo(@RequestBody RegistrationInfoRequest request) {
        log.info("Registration Request: {}", request);
        RegistrationInfoResponse response = registrationInfoService.createRegistrationInfo(request);
        log.info("Registration Response: {}", response);

        ApiResponse<RegistrationInfoResponse> apiResponse = ApiResponse.<RegistrationInfoResponse>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-not-registered")
    public ResponseEntity<ApiResponse<List<RegistrationInfoResponse>>> getNotRegistered(@RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        List<RegistrationInfoResponse> lstNotRegistered = registrationInfoService.getAllRegistrationInfoNotRegistered(pageSize,page);
        ApiResponse<List<RegistrationInfoResponse>> apiResponse = ApiResponse.<List<RegistrationInfoResponse>>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(lstNotRegistered)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/register/{id}")
    public ResponseEntity<ApiResponse<String>> updateRegistrationInfo(@PathVariable("id") String id) {
        log.info("Update Registration Info: {}", id);
        String response = registrationInfoService.changeToRegistered(id);
        log.info("Registration Response: {}", response);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
