package com.pinyincentre.pinyin.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutResponse {
    private String checkoutUrl;
    private Long orderCode;
}
