package com.pinyincentre.pinyin.service.classes;

import com.pinyincentre.pinyin.dto.request.ClassRequest;
import com.pinyincentre.pinyin.dto.response.ClassResponse;
import com.pinyincentre.pinyin.entity.Classroom;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.ClassRepository;
import com.pinyincentre.pinyin.repository.CourseRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "CLASS-SERVICE")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public ClassResponse getClassById(String id) {
        ClassResponse response = classRepository.findClassById(id);
        log.info("Class response {}", response);
        if(response == null) {
            log.warn(ErrorCode.CLASS_NOT_FOUND.getMessage());
            return null;
        }
        return response;
    }

    @Override
    public List<ClassResponse> getClassByCourseId(String courseId) {
        List<ClassResponse> response = classRepository.findClassByCourseId(courseId);
        log.info("Class response {}", response);
        if(response == null) {
            log.warn(ErrorCode.CLASS_NOT_FOUND.getMessage());
        }
        return response;
    }

    @Override
    public List<Object[]> getAllClassesPagination(Integer pageSize, int currentPage) {
        log.info("Current page: {}, page size: {}", currentPage, pageSize);

        int pageIndex = currentPage - 1;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<Object[]> page = classRepository.findAllClassesByStatusPagination(ClassStatus.ACTIVE.getStatus(), pageable);
        return page.getContent();
    }

    @Override
    public ClassResponse createClass(ClassRequest classRequest) {
        String fullName;
        String courseName;
        ClassResponse classResponse = new ClassResponse();
        log.info("Class response {}", classRequest);
        Classroom classroom = classroomMapper.toClassroom(classRequest);
        log.info("Class response {}", classroom);
        if(classroom == null) {
            log.warn(ErrorCode.SAVE_CLASS_ERROR.getMessage());
            return null;
        }
        classroom.setIsDelete(ClassStatus.ACTIVE.getStatus());
        Classroom savedClassroom = classRepository.save(classroom);
        log.info("Class response {}", savedClassroom);

        fullName = userRepository.findFullNameById(savedClassroom.getTeacherId());
        courseName = courseRepository.findCourseNameById(savedClassroom.getCourseId());

        classResponse.setTeacherName(fullName);
        classResponse.setCourseName(courseName);
        classResponse.setClassName(savedClassroom.getClassName());
        classResponse.setMaxStudents(savedClassroom.getMaxStudents());
        classResponse.setStartDate(classroomMapper.map(savedClassroom.getStartDate()));
        classResponse.setEndDate(classroomMapper.map(savedClassroom.getEndDate()));

        log.info("Class response {}", classResponse);
        return classResponse;
    }

    @Override
    public ClassResponse updateClass(ClassRequest classRequest, String id) {
        String fullName;
        String courseName;
        ClassResponse classResponse = new ClassResponse();
        Classroom classroom = classRepository.findByClassId(id);
        log.info("Class find {}", classroom);
        if(classroom == null) {
            log.warn(ErrorCode.CLASS_NOT_FOUND.getMessage());
            return null;
        }

        classroomMapper.updateClassroomFromRequest(classRequest, classroom);
        Classroom savedClassroom = classRepository.save(classroom);
        log.info("Class saved in update {}", savedClassroom);

        fullName = userRepository.findFullNameById(savedClassroom.getTeacherId());
        courseName = courseRepository.findCourseNameById(savedClassroom.getCourseId());

        classResponse.setTeacherName(fullName);
        classResponse.setCourseName(courseName);
        classResponse.setClassName(savedClassroom.getClassName());
        classResponse.setMaxStudents(savedClassroom.getMaxStudents());
        classResponse.setStartDate(classroomMapper.map(savedClassroom.getStartDate()));
        classResponse.setEndDate(classroomMapper.map(savedClassroom.getEndDate()));

        log.info("Class response {}", classResponse);
        return classResponse;
    }

    @Override
    public ClassResponse deleteClass(String id) {
        String fullName;
        String courseName;
        ClassResponse classResponse = new ClassResponse();
        Classroom classroom = classRepository.findByClassId(id);
        log.info("Class finding {}", classroom);
        if(classroom == null) {
            log.warn(ErrorCode.CLASS_NOT_FOUND.getMessage());
            return null;
        }

        classroom.setIsDelete(ClassStatus.DElETE.getStatus());
        Classroom savedClassroom = classRepository.save(classroom);
        log.info("Class saved {}", savedClassroom);

        fullName = userRepository.findFullNameById(savedClassroom.getTeacherId());
        courseName = courseRepository.findCourseNameById(savedClassroom.getCourseId());

        classResponse.setTeacherName(fullName);
        classResponse.setCourseName(courseName);
        classResponse.setClassName(savedClassroom.getClassName());
        classResponse.setMaxStudents(savedClassroom.getMaxStudents());
        classResponse.setStartDate(classroomMapper.map(savedClassroom.getStartDate()));
        classResponse.setEndDate(classroomMapper.map(savedClassroom.getEndDate()));

        log.info("Class response {}", classResponse);
        return classResponse;
    }
}
