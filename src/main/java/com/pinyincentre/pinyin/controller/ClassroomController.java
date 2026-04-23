package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.ClassRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.ClassResponse;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.entity.Classroom;
import com.pinyincentre.pinyin.entity.Schedule;
import com.pinyincentre.pinyin.entity.UserClass;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.*;
import com.pinyincentre.pinyin.service.classes.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classroom")
@Slf4j(topic = "CLASS-CONTROLLER")
public class ClassroomController {

    @Autowired
    private ClassService classService;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserClassRepository userClassRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

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

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createClass(@RequestBody Map<String, Object> request) {
        try {
            // Tạo lớp học
            Classroom cls = new Classroom();
            cls.setId(UUID.randomUUID().toString());
            cls.setName((String) request.get("className"));
            cls.setCourseId((String) request.get("courseId"));
            cls.setTeacherId((String) request.get("teacherId"));
            cls.setCreatedDate(LocalDateTime.now());
            
            if (request.get("startDate") != null) {
                cls.setStartDate(LocalDate.parse((String) request.get("startDate")).atStartOfDay());
            }
            if (request.get("endDate") != null) {
                cls.setEndDate(LocalDate.parse((String) request.get("endDate")).atStartOfDay());
            }
            
            if (request.get("maxStudents") != null) {
                cls.setMaxStudents(Integer.parseInt(request.get("maxStudents").toString()));
            }
            
            cls.setIsDelete(false);
            classRepository.save(cls);
 
            // Tạo lịch học
            List<Map<String, String>> schedules = (List<Map<String, String>>) request.get("schedules");
            if (schedules != null) {
                for (Map<String, String> s : schedules) {
                    Schedule schedule = new Schedule();
                    schedule.setClassId(cls.getId());
                    schedule.setClassDate(LocalDate.parse(s.get("classDate")));
                    schedule.setStartTime(LocalTime.parse(s.get("startTime")));
                    schedule.setEndTime(LocalTime.parse(s.get("endTime")));
                    schedule.setDescription(s.get("description"));
                    schedule.setLink(s.get("link"));
                    schedule.setCreatedDate(LocalDateTime.now());
                    schedule.setIsDelete(false);
                    scheduleRepository.save(schedule);
                }
            }
 
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Class created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Failed to create class: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getClassList() {
        try {
            List<Classroom> classes = classRepository.findAll();
            List<Map<String, Object>> classList = classes.stream().map(cls -> {
                String courseName = "Không xác định";
                if (cls.getCourseId() != null) {
                    courseName = courseRepository.findById(cls.getCourseId())
                            .map(c -> c.getCourseName())
                            .orElse("Không xác định");
                }

                String teacherName = "Không xác định";
                if (cls.getTeacherId() != null) {
                    teacherName = userRepository.findById(cls.getTeacherId())
                            .map(u -> u.getFullName())
                            .orElse("Không xác định");
                }

                long studentCount = userClassRepository.countByClassId(cls.getId());

                Map<String, Object> item = new HashMap<>();
                item.put("id", cls.getId());
                item.put("name", cls.getName());
                item.put("course_name", courseName);
                item.put("teacher_name", teacherName);
                item.put("start_date", cls.getStartDate() != null ? cls.getStartDate().toString() : "");
                item.put("end_date", cls.getEndDate() != null ? cls.getEndDate().toString() : "");
                item.put("student_count", studentCount);

                return item;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "result", classList
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "Lỗi server: " + e.getMessage()
            ));
        }

    }
    // Lấy thông tin lớp học theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClassById(@PathVariable String id) {
        try {
            Classroom classroom = classRepository.findByClassId(id);
            List<Schedule> schedules = scheduleRepository.findActiveSchedulesByClassId(id);
            
            Map<String, Object> classData = new HashMap<>();
            classData.put("id", classroom.getId());
            classData.put("className", classroom.getName() != null ? classroom.getName() : "");
            classData.put("courseId", classroom.getCourseId() != null ? classroom.getCourseId() : "");
            classData.put("teacherId", classroom.getTeacherId() != null ? classroom.getTeacherId() : "");
            classData.put("startDate", classroom.getStartDate() != null ? classroom.getStartDate().toLocalDate().toString() : "");
            classData.put("endDate", classroom.getEndDate() != null ? classroom.getEndDate().toLocalDate().toString() : "");
            classData.put("maxStudents", classroom.getMaxStudents());
            
            List<Map<String, Object>> scheduleList = schedules.stream().map(s -> {
                Map<String, Object> sMap = new HashMap<>();
                sMap.put("id", s.getId());
                sMap.put("classDate", s.getClassDate() != null ? s.getClassDate().toString() : "");
                sMap.put("startTime", s.getStartTime() != null ? s.getStartTime().toString() : "");
                sMap.put("endTime", s.getEndTime() != null ? s.getEndTime().toString() : "");
                sMap.put("link", s.getLink() != null ? s.getLink() : "");
                sMap.put("description", s.getDescription() != null ? s.getDescription() : "");
                return sMap;
            }).collect(Collectors.toList());
            
            classData.put("schedules", scheduleList);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("result", classData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("message", "Lỗi khi lấy lớp: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Cập nhật lớp học
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClass(
            @PathVariable String id,
            @RequestBody Map<String, Object> request
    ) {
        try {
//            Classroom classroom = classRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học"));
            Classroom classroom = classRepository.findByClassId(id);


            classroom.setName((String) request.get("className"));
            classroom.setCourseId((String) request.get("courseId"));
            classroom.setTeacherId((String) request.get("teacherId"));
            
            if (request.get("startDate") != null && !((String) request.get("startDate")).isEmpty()) {
                classroom.setStartDate(LocalDate.parse((String) request.get("startDate")).atStartOfDay());
            }
            
            if (request.get("endDate") != null && !((String) request.get("endDate")).isEmpty()) {
                classroom.setEndDate(LocalDate.parse((String) request.get("endDate")).atStartOfDay());
            }

            if (request.get("maxStudents") != null) {
                classroom.setMaxStudents(Integer.parseInt(request.get("maxStudents").toString()));
            }

            classRepository.save(classroom);

            // Xử lý lịch học
            List<Map<String, Object>> schedulesInput = (List<Map<String, Object>>) request.get("schedules");
            List<Schedule> existingSchedules = scheduleRepository.findActiveSchedulesByClassId(id);

            // Xóa các schedule không còn trong input
            for (Schedule existing : existingSchedules) {
                boolean exists = schedulesInput.stream().anyMatch(
                        s -> s.get("id") != null && Long.valueOf(s.get("id").toString()).equals(existing.getId())
                );
                if (!exists) {
                    existing.setIsDelete(true);
                    scheduleRepository.save(existing);
                }
            }

            // Thêm hoặc cập nhật schedule
            for (Map<String, Object> s : schedulesInput) {
                Schedule schedule;
                if (s.get("id") != null) {
                    schedule = scheduleRepository.findById(Long.valueOf(s.get("id").toString()))
                            .orElse(new Schedule());
                } else {
                    schedule = new Schedule();
                    schedule.setClassId(id);
                }
                schedule.setClassDate(LocalDate.parse((String) s.get("classDate")));
                schedule.setStartTime(LocalTime.parse((String) s.get("startTime")));
                schedule.setEndTime(LocalTime.parse((String) s.get("endTime")));
                schedule.setLink((String) s.get("link"));
                schedule.setDescription((String) s.get("description"));
                schedule.setIsDelete(false);
                scheduleRepository.save(schedule);
            }

            return ResponseEntity.ok(Map.of("status", 200, "message", "Cập nhật lớp thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Lỗi khi cập nhật lớp: " + e.getMessage()
            ));
        }
    }
    @PostMapping("/add-student")
    public ResponseEntity<Map<String, Object>> addStudentToClass(
            @RequestBody Map<String, Object> request
    ) {
        try {
            String classId = (String) request.get("class_id");
            List<String> userIds = (List<String>) request.get("user_ids");

            for (String userId : userIds) {
                UserClass userClass = new UserClass();
                userClass.setUserId(userId);
                userClass.setClassId(classId);
                userClassRepository.save(userClass);
            }

            return ResponseEntity.ok(Map.of("status", 200, "message", "Thêm học sinh thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message","Lỗi khi thêm học sinh: " + e.getMessage()
            ));
        }
    }
    @PostMapping("/remove-student")
    public ResponseEntity<Map<String, Object>> removeStudentFromClass(
            @RequestBody Map<String, Object> request
    ) {
        try {
            String classId = (String) request.get("class_id");
            List<String> userIds = (List<String>) request.get("user_ids");

            // Kiểm tra lớp có tồn tại
            UUID classUUID = UUID.fromString(classId);
            if (!classRepository.existsById( classUUID)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "message","Lớp không tồn tại"
                ));
            }

            // Xóa các bản ghi trong user_class
            for (String userId : userIds) {
                userClassRepository.deleteByUserIdAndClassId(userId, classId);
            }

            return ResponseEntity.ok(Map.of("status", 200, "message","Xóa học sinh thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Lỗi khi xóa học sinh: " + e.getMessage()
            ));
        }
    }
}
