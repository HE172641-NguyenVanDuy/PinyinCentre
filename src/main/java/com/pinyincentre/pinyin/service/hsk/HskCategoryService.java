package com.pinyincentre.pinyin.service.hsk;

import com.pinyincentre.pinyin.dto.request.HskCategoryRequest;
import com.pinyincentre.pinyin.dto.response.HskCategoryResponse;

import java.util.List;

public interface HskCategoryService {
    List<HskCategoryResponse> getAllCategories();
    HskCategoryResponse getCategoryById(String id);
    String createCategory(HskCategoryRequest request);
    String updateCategory(String id, HskCategoryRequest request);
    String deleteCategory(String id);
}
