package com.pinyincentre.pinyin.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LibraryRequest {
    private String title;
    private String fileLink;
    private String courseType;
    private String description;
    //private String status;

}
