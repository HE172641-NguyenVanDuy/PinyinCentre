package com.pinyincentre.pinyin.service.registration_info;

import com.pinyincentre.pinyin.dto.request.RegistrationInfoRequest;
import com.pinyincentre.pinyin.dto.response.RegistrationInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegistrationInfoService {

    RegistrationInfoResponse getRegistrationInfoById(String id);
    String changeToRegistered(String id);
    RegistrationInfoResponse createRegistrationInfo(RegistrationInfoRequest request);
    List<RegistrationInfoResponse> getAllRegistrationInfoNotRegistered(Integer pageSize, int offset);
}
