package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.RoleRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository roleRepository;

    private static final int PAGE_SIZE = 10;


    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Transactional
    @Override
    public UserResponse createUser(UserRequest request) throws IOException {
        String testPass = "12345678";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String passwordGenerate;
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.EXIST_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EXIST_EMAIL);
        }

        UserEntity userEntity = userMapper.toUser(request);
        userEntity.setStatus(UserStatus.ACTIVE.getCode());

        // set role
        RoleEntity roleEntity = roleRepository.findById("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role 'STUDENT' not found in DB"));

        userEntity.setRoleEntities(Set.of(roleEntity));

        // hash password
        passwordGenerate = testPass;
        userEntity.setPassword(passwordEncoder.encode(passwordGenerate));

        return userMapper.toUserResponse(userRepository.save(userEntity));
    }

    @PreAuthorize("@securityUtil.isCurrentUserId(#uid)")
    @Transactional
    @Override
    public UserResponse updateUser(UserUpdateRequest request, String uid) {
        log.info("User request: {}", request);
        UserEntity userEntity = userRepository.findById(uid).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        log.info("User information: {},  {}", userEntity, userEntity.getDob());
        userMapper.updateUserFromRequest(request, userEntity);
        userEntity.setUpdatedDate(LocalDateTime.now());

//        var roles = roleRepository.findAllById(request.getRoles());
//        user.setRoles(new HashSet<>(roles));
        log.info("User updated: {}", userEntity);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        log.info("User after update: {}", savedUserEntity);
        return userMapper.toUserResponse(savedUserEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Transactional
    @Override
    public String banUser(String uid) {
        if(uid.isEmpty()) {
            return ErrorCode.USER_ID_EMPTY.getMessage();
        }
        
        UserEntity user = userRepository.findById(uid).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        int newStatus = (user.getStatus() == UserStatus.ACTIVE.getCode()) ? UserStatus.BAN.getCode() : UserStatus.ACTIVE.getCode();
        
        int count = userRepository.changeStatusUserById(uid, newStatus);
        log.info("Number of count change the row: {}, New status: {}", count, newStatus);
        
        if(count > 0) {
            return newStatus == UserStatus.BAN.getCode() ? "Khóa người dùng thành công" : "Mở khóa người dùng thành công";
        } else {
            return "Thao tác thất bại";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Override
    public List<UserResponseProjection> getAllUsersActive(Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        log.info("Current page: {}, page size: {}", currentPage, pageSize);
        int offset = (currentPage - 1) * pageSize;

        return userRepository.listUserPagination(UserStatus.ACTIVE.getCode(), pageSize, offset);
    }

    @PreAuthorize("@securityUtil.isCurrentUserId(#uid)")
    @Override
    public UserResponse getUserById(String uid) {
        UserEntity userEntity = userRepository.findById(uid).orElseThrow(
          () -> new AppException(ErrorCode.NOT_FOUND)
        );
        return userMapper.toUserResponse(userEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Override
    public List<UserResponseProjection> getUserByRole(String role, Integer status, Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        if (status == null) {
            status = UserStatus.ACTIVE.getCode();
        }
        log.info("Current page: {}, page size: {}, status: {}", currentPage, pageSize, status);
        int offset = (currentPage - 1) * pageSize;
        return userRepository.getListUserByRole(role, status, pageSize, offset);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Override
    public UserResponse createTeacherAccount(UserRequest request) throws IOException {
        String testPass = "12345678";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String passwordGenerate;
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.EXIST_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EXIST_EMAIL);
        }

        UserEntity userEntity = userMapper.toUser(request);
        userEntity.setStatus(UserStatus.ACTIVE.getCode());

        // set role
        RoleEntity roleEntity = roleRepository.findById("TEACHER")
                .orElseThrow(() -> new RuntimeException("Role 'TEACHER' not found in DB"));

        userEntity.setRoleEntities(Set.of(roleEntity));

        // hash password
        passwordGenerate = testPass;
        userEntity.setPassword(passwordEncoder.encode(passwordGenerate));

        return userMapper.toUserResponse(userRepository.save(userEntity));
    }

    @Cacheable(value = "userRoles", key = "#username")
    @Override
    public Set<String> getRoleNames(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRoleEntities()
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public UserEntity getUserByUserName(String userName) {
        return userRepository.getFirstByUsername(userName);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Override
    public List<UserResponseProjection> getStudentsInClass(String classId) {
        return userRepository.getStudentsInClass(classId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @Override
    public List<UserResponseProjection> getStudentsNotInClass(String classId) {
        return userRepository.getStudentsNotInClass(classId);
    }
}
