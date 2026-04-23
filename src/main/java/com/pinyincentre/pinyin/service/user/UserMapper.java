package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.dto.request.UserRequest;
import com.pinyincentre.pinyin.dto.request.UserUpdateRequest;
import com.pinyincentre.pinyin.dto.response.PermissionResponse;
import com.pinyincentre.pinyin.dto.response.RoleResponse;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.Permission;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.service.utils.DateMapper;
import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface UserMapper {

    UserEntity toUser(UserRequest request);

    // Sử dụng chung logic mapping cho UserResponse
    @Mappings({
            @Mapping(source = "createdDate", target = "createDate"),
            @Mapping(source = "updatedDate", target = "updateDate"),
            @Mapping(source = "expiredDate", target = "expireDate"),
            @Mapping(source = "roleEntities", target = "roles"),
            @Mapping(source = "status", target = "status", qualifiedByName = "statusIntToString")
    })
    UserResponse toUserResponse(UserEntity userEntity);

    @Mappings({
            @Mapping(source = "dob", target = "dob", qualifiedByName = "toLocalDateTime"),
            @Mapping(target = "roleEntities", ignore = true)
    })
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget UserEntity userEntity);

    // --- MAPPING CHO ROLE & PERMISSION ---

    RoleResponse toRoleResponse(RoleEntity roleEntity);

    PermissionResponse toPermissionResponse(Permission permissionEntity);

    // --- CUSTOM LOGIC (CHỈ GIỮ LẠI 1 BẢN) ---

    @Named("statusIntToString")
    default String statusIntToString(int status) {
        try {
            // Ưu tiên dùng Enum UserStatus nếu bạn đã định nghĩa
            return UserStatus.fromCode(status).getStatusName();
        } catch (Exception e) {
            return String.valueOf(status);
        }
    }

    // MapStruct sẽ tự dùng 2 hàm này cho tất cả các trường LocalDateTime <-> Timestamp
    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}

