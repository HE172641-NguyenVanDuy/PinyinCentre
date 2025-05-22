package com.pinyincentre.pinyin.service.registration_info;

import com.pinyincentre.pinyin.dto.request.RegistrationInfoRequest;
import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.response.RegistrationInfoResponse;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.RegistrationInfo;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.RegistrationInfoRepository;
import com.pinyincentre.pinyin.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "REGISTRATION-SERVICE")
public class RegistrationInfoServiceImpl implements RegistrationInfoService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private RegistrationInfoRepository registrationInfoRepository;

    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;

    @Autowired
    private UserService userService;

    @Override
    public RegistrationInfoResponse getRegistrationInfoById(String id) {
        RegistrationInfoResponse response = registrationInfoRepository.findByUUID(id);
        log.info("getRegistrationInfoById: {}", response);
        if(response == null) {
            log.warn("RegistrationInfo not found for id: {}", id);
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return response;
    }

//    @Transactional
//    @Override
//    public String changeToRegistered(String id) {
//        int isRegistered = 1;
//        int count = registrationInfoRepository.updateIsRegistered(isRegistered,id);
//        log.info("Number of change row: {}",count);
//        if(count >  0)
//            return ErrorCode.CHANGE_IS_REGISTERED.getMessage();
//        if(id == null) {
//            log.warn("Update failed: ID is null.  No record found to update. Please verify the provided ID.");
//            return ErrorCode.FAIL_CHANGE_IS_REGISTERED.getMessage();
//        }
//        if (isRegistered != 0 && isRegistered != 1) {
//            log.warn("Invalid value for isRegistered: {}.  Expected 0 or 1.", isRegistered);
//            return ErrorCode.FAIL_CHANGE_IS_REGISTERED.getMessage();
//        }
//        return ErrorCode.FAIL_CHANGE_IS_REGISTERED.getMessage();
//    }

    @Transactional
    @Override
    public String changeToRegistered(String id) {
        int count;
        RegistrationInfoResponse registrationResponse = getRegistrationInfoById(id);

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(registrationResponse.getEmail());
        userRequest.setUsername(registrationResponse.getEmail().substring(0, registrationResponse.getEmail().indexOf("@")));
        userRequest.setFullName(registrationResponse.getFullName());
        userRequest.setPhoneNumber(registrationResponse.getPhoneNumber());

        UserResponse userResponse = userService.createUser(userRequest);

        if(userResponse != null) {
            count = registrationInfoRepository.updateIsRegistered(RegistrationStatus.REGISTERED.getCode(), id);
            log.info("Number of change row: {}",count);
            if(count > 0) return ErrorCode.CHANGE_IS_REGISTERED.getMessage();
        }
        return ErrorCode.FAIL_CHANGE_IS_REGISTERED.getMessage();
    }

    @Transactional
    @Override
    public RegistrationInfoResponse createRegistrationInfo(RegistrationInfoRequest request) {
        RegistrationInfo registrationInfo;
        try{
            log.info("Received request: {}", request.toString());
            registrationInfo = registrationInfoMapper.toRegistrationInfo(request);
            log.info("Mapped entity: {}", registrationInfo.toString());
            if (registrationInfo.getCourseId() == null
                    || registrationInfo.getEmail() == null
                    || registrationInfo.getFullName() == null
                    || registrationInfo.getPhoneNumber() == null) {
                throw new AppException( ErrorCode.REGISTRATION_NOTNULL_FIELD);
            }
            return registrationInfoMapper.toRegistrationResponse(registrationInfoRepository.save(registrationInfo));
        } catch (Exception e) {
            log.error("Error creating registration info", e);
            throw new RuntimeException(ErrorCode.REGISTRATION_FAIL.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationInfoResponse> getAllRegistrationInfoNotRegistered(Integer pageSize, int currentPage) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        log.info("Current page: {}, page size: {}", currentPage, pageSize);
        int offset = (currentPage - 1) * pageSize; // Tính offset
//        log.info("Size list: {}", registrationInfoRepository.getListNotRegistratedInfoWithPagination(pageSize, offset).size());
        return registrationInfoRepository.getListNotRegistratedInfoWithPagination(pageSize, offset);
    }
}
