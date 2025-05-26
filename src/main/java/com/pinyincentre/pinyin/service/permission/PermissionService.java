package com.pinyincentre.pinyin.service.permission;

import com.pinyincentre.pinyin.dto.request.PermissionRequest;
import com.pinyincentre.pinyin.dto.response.PermissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PermissionService {

    PermissionResponse createPermission(PermissionRequest request);
    List<PermissionResponse> getAll();
    void delete(String name);
}
