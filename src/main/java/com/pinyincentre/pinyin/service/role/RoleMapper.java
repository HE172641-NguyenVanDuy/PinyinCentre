package com.pinyincentre.pinyin.service.role;

import com.pinyincentre.pinyin.dto.request.RoleRequest;
import com.pinyincentre.pinyin.dto.response.RoleResponse;
import com.pinyincentre.pinyin.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}