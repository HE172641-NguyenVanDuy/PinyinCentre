package com.pinyincentre.pinyin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
@Setter
public class PermissionResponse {
    String name;
    String description;
}
