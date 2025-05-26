package com.pinyincentre.pinyin.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PermissionRequest {
    String name;
    String description;
}
