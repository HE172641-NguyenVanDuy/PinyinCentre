package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.ClassRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.ClassResponse;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.service.classes.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@Slf4j(topic = "CLASS-CONTROLLER")
public class ClassroomController {

    @Autowired
    private ClassService classService;

    @GetMapping("/get-all-class-active")
    public ResponseEntity<ApiResponse<List<Object[]>>> getAllFilesInLibrary(@RequestParam(defaultValue = "1") int page,
                                                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<Object[]> list = classService.getAllClassesPagination(pageSize, page);
        ApiResponse<List<Object[]>> apiResponse = ApiResponse.<List<Object[]>>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(list)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-class")
    public ResponseEntity<ApiResponse<ClassResponse>> createClassroom(@RequestBody ClassRequest classRequest) {
        ClassResponse classResponse = classService.createClass(classRequest);
        ApiResponse<ClassResponse> apiResponse = ApiResponse.<ClassResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(classResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-class/{id}")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClassroom(@RequestBody ClassRequest classRequest, @PathVariable("id") String id) {
        ClassResponse classResponse = classService.updateClass(classRequest, id);
        ApiResponse<ClassResponse> apiResponse = ApiResponse.<ClassResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(classResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete-class/{id}")
    public ResponseEntity<ApiResponse<ClassResponse>> deleteClassroom(@PathVariable("id") String id) {
        ClassResponse classResponse = classService.deleteClass(id);
        ApiResponse<ClassResponse> apiResponse = ApiResponse.<ClassResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(classResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-class-by-id/{id}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassroomById(@PathVariable("id") String id) {
        ClassResponse classResponse = classService.getClassById(id);
        ApiResponse<ClassResponse> apiResponse = ApiResponse.<ClassResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(classResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-class-by-course-id/{id}")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassroomByCourseId(@PathVariable("id") String id) {
        List<ClassResponse> classResponse = classService.getClassByCourseId(id);
        ApiResponse<List<ClassResponse>> apiResponse = ApiResponse.<List<ClassResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(classResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
