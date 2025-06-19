package com.pinyincentre.pinyin.service.course;

import com.pinyincentre.pinyin.dto.request.CourseRequest;
import com.pinyincentre.pinyin.dto.response.CourseResponse;
import com.pinyincentre.pinyin.entity.Course;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "COURSE-SERVICE")
public class CourseServiceImpl implements CourseService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;


    @Override
    public List<CourseResponse> getAllCoursesActive(Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        log.info("Current page: {}, page size: {}", currentPage, pageSize);
        int offset = (currentPage - 1) * pageSize; // Tính offset
        return courseRepository.getAllCourseActiveWithPagination(pageSize, offset);
    }

    @Override
    public CourseResponse getCourseById(String id) {
        CourseResponse response = courseRepository.getCourseById(id);
        if(response == null) {
            log.warn("Course with id {} not found", id);
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Transactional
    @Override
    public String createCourse(CourseRequest request) {

        Course course = courseMapper.toCourse(request);
        log.info("Mapped course object: {}", course);
        if(course != null) {
            log.info("Course created: {}", course);
            course.setIsDelete(CourseStatus.ACTIVE.getCode());
            courseRepository.save(course);
            return ErrorCode.CREATE_COURSE_SUCCESS.getMessage();
        }

        return ErrorCode.CREATE_COURSE_FAIL.getMessage();
    }
}
