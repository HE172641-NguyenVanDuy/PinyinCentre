package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.entity.Notification;
import com.pinyincentre.pinyin.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    private String getUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaimAsString("userId");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(Authentication authentication) {
        String userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<List<Notification>>builder()
                .status(200)
                .result(notificationService.getNotificationsForUser(userId))
                .build());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        String userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .status(200)
                .result(notificationService.getUnreadCount(userId))
                .build());
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().status(200).build());
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        String userId = getUserId(authentication);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().status(200).build());
    }
}
