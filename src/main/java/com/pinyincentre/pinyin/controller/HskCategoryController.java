package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.HskCategoryRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.HskCategoryResponse;
import com.pinyincentre.pinyin.service.hsk.HskCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HskCategoryController {

    private final HskCategoryService hskCategoryService;

    @GetMapping("/public/hsk-categories")
    public ResponseEntity<ApiResponse<List<HskCategoryResponse>>> getAllPublicCategories() {
        return ResponseEntity.ok(ApiResponse.<List<HskCategoryResponse>>builder()
                .status(200)
                .result(hskCategoryService.getAllCategories())
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRE_OWNER')")
    @GetMapping("/hsk-categories")
    public ResponseEntity<ApiResponse<List<HskCategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.<List<HskCategoryResponse>>builder()
                .status(200)
                .result(hskCategoryService.getAllCategories())
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRE_OWNER')")
    @PostMapping("/hsk-categories")
    public ResponseEntity<ApiResponse<String>> createCategory(@RequestBody HskCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .result(hskCategoryService.createCategory(request))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRE_OWNER')")
    @PutMapping("/hsk-categories/{id}")
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable String id, @RequestBody HskCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .result(hskCategoryService.updateCategory(id, request))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRE_OWNER')")
    @DeleteMapping("/hsk-categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .result(hskCategoryService.deleteCategory(id))
                .build());
    }
}
