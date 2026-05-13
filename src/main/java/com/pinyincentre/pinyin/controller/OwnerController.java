package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/revenue-stats")
    @PreAuthorize("hasRole('CENTRE_OWNER')")
    public ApiResponse<Map<String, Object>> getRevenueStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRevenue", paymentRepository.getTotalRevenue());
        stats.put("totalPaidStudents", paymentRepository.getTotalPaidStudents());
        
        List<Map<String, Object>> revenueByCourse = paymentRepository.getRevenueByCourse().stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("courseName", row[0]);
                    item.put("revenue", row[1]);
                    return item;
                })
                .collect(Collectors.toList());
        
        stats.put("revenueByCourse", revenueByCourse);
        
        return ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .result(stats)
                .build();
    }
}
