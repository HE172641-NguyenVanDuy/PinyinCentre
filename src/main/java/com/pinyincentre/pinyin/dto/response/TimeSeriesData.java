package com.pinyincentre.pinyin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesData {
    private String label; // e.g., "2024-05-01" or "May"
    private long value;
}
