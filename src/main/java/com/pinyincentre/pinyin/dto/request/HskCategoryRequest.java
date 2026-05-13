package com.pinyincentre.pinyin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HskCategoryRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    private String description;
}
