package com.pinyincentre.pinyin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActiveAccountDTO {
    @NotNull(message = "Token không được để trống")
    private String token;
}

