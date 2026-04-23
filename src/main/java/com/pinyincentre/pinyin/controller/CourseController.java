package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.CourseRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.CourseResponse;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.service.course.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@Slf4j(topic = "COURSE-CONTROLLER")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createCourse(@RequestBody @Valid CourseRequest courseRequest) {
        log.info("Course request: {}", courseRequest);
        String response = courseService.createCourse(courseRequest);
        log.info("Course response: {}", response);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-course")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCoursesActiveWithPagination(@RequestParam(defaultValue = "1") int page,
                                                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        log.info("Current Page: {}, Page Size: {}", page, pageSize);
        List<CourseResponse> lstCourse = courseService.getAllCoursesActive(pageSize, page);
        log.info("Course list: {}", lstCourse.size());
        ApiResponse<List<CourseResponse>> apiResponse = ApiResponse.<List<CourseResponse>>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(lstCourse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-course/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable String id) {
        CourseResponse response = courseService.getCourseById(id);
        ApiResponse<CourseResponse> apiResponse = ApiResponse.<CourseResponse>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCourse(@PathVariable String id, @RequestBody @Valid CourseRequest courseRequest) {
        String response = courseService.updateCourse(id, courseRequest);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCourse(@PathVariable String id) {
        String response = courseService.deleteCourse(id);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .status(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
