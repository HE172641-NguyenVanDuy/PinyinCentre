package com.pinyincentre.pinyin.service.payment;

import com.pinyincentre.pinyin.dto.request.CheckoutRequest;
import com.pinyincentre.pinyin.dto.response.CheckoutResponse;
import com.pinyincentre.pinyin.entity.*;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.*;
import com.pinyincentre.pinyin.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final UserClassRepository userClassRepository;
    private final EmailService emailService;

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    private PayOS payOS;

    private void initPayOS() {
        if (payOS == null) {
            payOS = new PayOS(clientId, apiKey, checksumKey);
        }
    }

    @Transactional
    public CheckoutResponse createPaymentLink(CheckoutRequest request) throws Exception {
        initPayOS();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        long orderCode = new Date().getTime() / 1000;
        Long amount = course.getPrice();

        // Create Payment record
        Payment payment = Payment.builder()
                .orderCode(orderCode)
                .amount(amount.intValue())
                .status("PENDING")
                .userId(user.getId())
                .courseId(course.getId())
                .build();
        paymentRepository.save(payment);

        // Prepare PayOS data
        String description = "Thanh toan khoa hoc " + course.getCourseName();
        // Frontend handles these redirects
        String returnUrl = "http://localhost:5173/student/my-courses?status=success";
        String cancelUrl = "http://localhost:5173/courses/hsk?status=cancelled";

        List<PaymentLinkItem> items = new ArrayList<>();
        items.add(PaymentLinkItem.builder()
                .name(course.getCourseName())
                .quantity(1)
                .price(amount)
                .build());

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .items(items)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

        return CheckoutResponse.builder()
                .checkoutUrl(data.getCheckoutUrl())
                .orderCode(orderCode)
                .build();
    }

    @Transactional
    public void handleWebhook(Webhook webhook) throws Exception {
        initPayOS();
        
        try {
            WebhookData data = payOS.webhooks().verify(webhook);
            log.info(">>> PayOS Webhook received and verified: orderCode={}, status={}", data.getOrderCode(), data.getDescription());
            
            // Handle PayOS test webhook (validation)
            if (data.getOrderCode() == 123) {
                log.info(">>> Received PayOS test webhook validation (orderCode=123). Bypassing DB check.");
                return;
            }

            if ("00".equals(data.getCode()) || "success".equalsIgnoreCase(data.getDescription())) {
                Payment payment = paymentRepository.findByOrderCode(data.getOrderCode())
                        .orElseThrow(() -> new RuntimeException("Payment record not found for orderCode: " + data.getOrderCode()));

                if ("PAID".equals(payment.getStatus())) {
                    log.info(">>> Order {} was already marked as PAID. Skipping.", data.getOrderCode());
                    return;
                }

                payment.setStatus("PAID");
                paymentRepository.save(payment);
                log.info(">>> Updated payment status to PAID for order: {}", data.getOrderCode());

                // Enrollment Logic
                processEnrollment(payment);
            } else {
                log.warn(">>> Webhook reported unsuccessful payment for order {}: code={}, desc={}", 
                        data.getOrderCode(), data.getCode(), data.getDescription());
            }
        } catch (Exception e) {
            log.error(">>> Error processing PayOS Webhook: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean isOrderProcessed(Long orderCode) {
        return paymentRepository.findByOrderCode(orderCode)
                .map(p -> "PAID".equals(p.getStatus()))
                .orElse(false);
    }

    private void processEnrollment(Payment payment) {
        log.info(">>> Starting enrollment for student ID: {} in course ID: {}", payment.getUserId(), payment.getCourseId());
        
        try {
            // Find all available classes and filter safely
            List<Classroom> classrooms = classRepository.findAll().stream()
                    .filter(c -> java.util.Objects.nonNull(c.getCourseId())) // Lọc bỏ lớp học không có courseId
                    .filter(c -> java.util.Objects.equals(c.getCourseId(), payment.getCourseId())) // So sánh an toàn
                    .filter(c -> !Boolean.TRUE.equals(c.getIsDelete()))
                    .toList();
            
            log.info(">>> Found {} candidate classrooms for course.", classrooms.size());

            Classroom targetClass = classrooms.stream()
                    .filter(c -> {
                        long currentCount = userClassRepository.countByClassId(c.getId());
                        return currentCount < c.getMaxStudents();
                    })
                    .findFirst()
                    .orElse(null);

            if (targetClass != null) {
                // Check if already enrolled (to prevent duplicates)
                boolean alreadyEnrolled = userClassRepository.findAll().stream()
                        .anyMatch(uc -> java.util.Objects.equals(uc.getUserId(), payment.getUserId()) 
                                    && java.util.Objects.equals(uc.getClassId(), targetClass.getId()));
                
                if (alreadyEnrolled) {
                    log.info(">>> Student already enrolled in class: {}. Skipping creation.", targetClass.getName());
                    return;
                }

                UserClass userClass = new UserClass();
                userClass.setUserId(payment.getUserId());
                userClass.setClassId(targetClass.getId());
                userClassRepository.save(userClass);
                log.info(">>> Successfully enrolled student in class: {}", targetClass.getName());

                // Send Email Notification
                sendNotificationEmail(payment, targetClass);
            } else {
                log.error(">>> CRITICAL: No available classroom found (full or deleted) for courseId: {}. Student ID {} needs manual enrollment.", 
                        payment.getCourseId(), payment.getUserId());
            }
        } catch (Exception e) {
            log.error(">>> ERROR in processEnrollment: {}. Payment was successful but student registration failed.", e.getMessage(), e);
        }
    }

    private void sendNotificationEmail(Payment payment, Classroom classroom) {
        UserEntity user = userRepository.findById(payment.getUserId()).orElse(null);
        Course course = courseRepository.findById(payment.getCourseId()).orElse(null);
        if (user != null && course != null) {
            try {
                emailService.sendEnrollmentEmail(user.getEmail(), user.getFullName(), course.getCourseName(), classroom.getName());
                log.info(">>> Confirmation email sent to {}", user.getEmail());
            } catch (Exception e) {
                log.error(">>> Failed to send confirmation email: {}", e.getMessage());
            }
        }
    }
}
