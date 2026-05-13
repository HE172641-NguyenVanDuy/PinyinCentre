package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.entity.UserEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Map<String, Object>>> getMyClasses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        
        return ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(classRepository.findClassesByStudentId(user.getId()))
                .build();
    }
}
