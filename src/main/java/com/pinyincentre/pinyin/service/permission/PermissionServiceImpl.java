package com.pinyincentre.pinyin.service.permission;

import com.pinyincentre.pinyin.dto.request.PermissionRequest;
import com.pinyincentre.pinyin.dto.response.PermissionResponse;
import com.pinyincentre.pinyin.entity.Permission;
import com.pinyincentre.pinyin.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {


    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PermissionMapper permissionMapper;


    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String name) {
        permissionRepository.deleteById(name);
    }

}
