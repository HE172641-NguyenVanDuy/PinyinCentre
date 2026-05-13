package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.CheckoutRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.CheckoutResponse;
import com.pinyincentre.pinyin.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) throws Exception {
        return ApiResponse.<CheckoutResponse>builder()
                .status(200)
                .result(paymentService.createPaymentLink(request))
                .build();
    }

    @PostMapping("/webhook")
    public void webhook(@RequestBody Webhook webhook) throws Exception {
        log.info("Webhook endpoint called with data: {}", webhook);
        paymentService.handleWebhook(webhook);
    }

    @GetMapping("/verify-order")
    public ApiResponse<java.util.Map<String, Object>> verifyOrder(@RequestParam Long orderCode) {
        boolean processed = paymentService.isOrderProcessed(orderCode);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", processed ? "SUCCESS" : "PENDING");
        response.put("isEnrolled", processed);

        return ApiResponse.<java.util.Map<String, Object>>builder()
                .status(200)
                .result(response)
                .build();
    }
}
