package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LibraryResponse {

    private Long id;
    private String title;
    private String fileLink;
    private String courseType;
    private String description;
    //private String status;
    private LocalDateTime createdDate;

}
