package com.pinyincentre.pinyin.service.hsk;

import com.pinyincentre.pinyin.dto.request.HskCategoryRequest;
import com.pinyincentre.pinyin.dto.response.HskCategoryResponse;
import com.pinyincentre.pinyin.entity.HskCategory;
import com.pinyincentre.pinyin.service.course.CourseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface HskCategoryMapper {
    HskCategory toHskCategory(HskCategoryRequest request);
    
    HskCategoryResponse toHskCategoryResponse(HskCategory hskCategory);

    void updateHskCategory(@MappingTarget HskCategory hskCategory, HskCategoryRequest request);
}
