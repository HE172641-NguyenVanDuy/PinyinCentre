package com.pinyincentre.pinyin.service.permission;

import com.pinyincentre.pinyin.dto.request.PermissionRequest;
import com.pinyincentre.pinyin.dto.response.PermissionResponse;
import com.pinyincentre.pinyin.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
