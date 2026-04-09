package com.pinyincentre.pinyin.service.role;

import com.pinyincentre.pinyin.dto.request.RoleRequest;
import com.pinyincentre.pinyin.dto.response.RoleResponse;
import com.pinyincentre.pinyin.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    RoleEntity toRole(RoleRequest request);

    RoleResponse toRoleResponse(RoleEntity roleEntity);
}