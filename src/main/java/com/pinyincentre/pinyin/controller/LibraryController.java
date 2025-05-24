package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.LibraryRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.service.library.LibraryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@Slf4j(topic = "LIBRARY-CONTROLLER")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/get-all-files")
    public ResponseEntity<ApiResponse<List<LibraryResponse>>> getAllFilesInLibrary(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<LibraryResponse> list = libraryService.getAllFilesInLibrary(pageSize, page);
        ApiResponse<List<LibraryResponse>> apiResponse = ApiResponse.<List<LibraryResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(list)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-files")
    public ResponseEntity<ApiResponse<LibraryResponse>> createFiles(@RequestBody LibraryRequest libraryRequest) {
        LibraryResponse response = libraryService.createFileLinkInLibrary(libraryRequest);
        ApiResponse<LibraryResponse> apiResponse = ApiResponse.<LibraryResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-files/{id}")
    public ResponseEntity<ApiResponse<LibraryResponse>> updateFiles(@PathVariable("id") Long id,
                                                                    @RequestBody LibraryRequest libraryRequest) {
        LibraryResponse response = libraryService.updateFileLinkInLibrary(libraryRequest, id);
        ApiResponse<LibraryResponse> apiResponse = ApiResponse.<LibraryResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete-files/{id}")
    public ResponseEntity<ApiResponse<LibraryResponse>> deleteFile(@PathVariable("id") Long id) {
        LibraryResponse response = libraryService.deleteFileInLibrary(id);
        ApiResponse<LibraryResponse> apiResponse = ApiResponse.<LibraryResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
