package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.service.utils.DateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface UserMapper {

    User toUser(UserRequest request);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdDate", target = "createDate"),
            @Mapping(source = "updatedDate", target = "updateDate"),
            @Mapping(source = "expiredDate", target = "expireDate"),
            @Mapping(source = "dob", target = "dob")
    })
    UserResponse toUserResponse(User user);

//    @Mappings({
//            @Mapping(source = "id", target = "id")
//            @Mapping(source = "createdDate", target = "createDate"),
//            @Mapping(source = "updatedDate", target = "updateDate"),
//            @Mapping(source = "expiredDate", target = "expireDate"),
//            @Mapping(source = "dob", target = "dob")
//})
    @Mapping(source = "dob", target = "dob", qualifiedByName = "toLocalDateTime")
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);

    default String mapStatus(int statusCode) {
        try {
            return UserStatus.fromCode(statusCode).getStatusName();
        } catch (IllegalArgumentException e) {
            return "Unknown"; // hoặc null hoặc ""
        }
    }

    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
