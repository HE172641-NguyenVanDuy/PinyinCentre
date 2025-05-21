package com.pinyincentre.pinyin.service.course;

import com.pinyincentre.pinyin.dto.request.CourseRequest;
import com.pinyincentre.pinyin.dto.response.CourseResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {

    List<CourseResponse> getAllCoursesActive(Integer pageSize, int currentPage);

    CourseResponse getCourseById(String id);

    String createCourse(CourseRequest course);
}
