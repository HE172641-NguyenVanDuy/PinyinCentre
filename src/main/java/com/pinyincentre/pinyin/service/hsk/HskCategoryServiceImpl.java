package com.pinyincentre.pinyin.service.hsk;

import com.pinyincentre.pinyin.dto.request.HskCategoryRequest;
import com.pinyincentre.pinyin.dto.response.HskCategoryResponse;
import com.pinyincentre.pinyin.entity.HskCategory;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.HskCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HskCategoryServiceImpl implements HskCategoryService {

    private final HskCategoryRepository hskCategoryRepository;
    private final HskCategoryMapper hskCategoryMapper;

    @Override
    public List<HskCategoryResponse> getAllCategories() {
        return hskCategoryRepository.findAll().stream()
                .filter(c -> c.getIsDelete() == null || !c.getIsDelete())
                .map(hskCategoryMapper::toHskCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HskCategoryResponse getCategoryById(String id) {
        HskCategory category = hskCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (category.getIsDelete() != null && category.getIsDelete()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return hskCategoryMapper.toHskCategoryResponse(category);
    }

    @Transactional
    @Override
    public String createCategory(HskCategoryRequest request) {
        if (hskCategoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        HskCategory category = hskCategoryMapper.toHskCategory(request);
        category.setIsDelete(false);
        hskCategoryRepository.save(category);
        return "Create HSK Category success";
    }

    @Transactional
    @Override
    public String updateCategory(String id, HskCategoryRequest request) {
        HskCategory category = hskCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!category.getName().equals(request.getName()) && hskCategoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        hskCategoryMapper.updateHskCategory(category, request);
        hskCategoryRepository.save(category);
        return "Update HSK Category success";
    }

    @Transactional
    @Override
    public String deleteCategory(String id) {
        HskCategory category = hskCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        
        // Soft delete category
        category.setIsDelete(true);
        
        // Soft delete all associated courses
        if (category.getCourses() != null) {
            category.getCourses().forEach(course -> course.setIsDelete(true));
        }
        
        hskCategoryRepository.save(category);
        return "Soft delete HSK Category success";
    }
}
