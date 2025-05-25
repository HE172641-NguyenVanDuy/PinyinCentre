package com.pinyincentre.pinyin.service.classes;

import com.pinyincentre.pinyin.dto.request.ClassRequest;
import com.pinyincentre.pinyin.dto.response.ClassResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassService {

    ClassResponse getClassById(String id);
    List<ClassResponse> getClassByCourseId(String courseId);
    List<Object[]> getAllClassesPagination(Integer pageSize, int currentPage);
    ClassResponse createClass(ClassRequest classRequest);
    ClassResponse updateClass(ClassRequest classRequest, String id);
    ClassResponse deleteClass(String id);
}
