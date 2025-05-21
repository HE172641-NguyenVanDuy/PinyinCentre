package com.pinyincentre.pinyin.service.registration_info;

import com.pinyincentre.pinyin.dto.request.RegistrationInfoRequest;
import com.pinyincentre.pinyin.dto.response.RegistrationInfoResponse;
import com.pinyincentre.pinyin.entity.RegistrationInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RegistrationInfoMapper {

    @Mapping(target = "courseId", source = "courseId")
    RegistrationInfo toRegistrationInfo(RegistrationInfoRequest request);
    RegistrationInfoResponse toRegistrationResponse(RegistrationInfo registrationInfo);

    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
