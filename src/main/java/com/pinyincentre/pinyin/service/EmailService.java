package com.pinyincentre.pinyin.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailService {

    private final JavaMailSender mailSender;
//    private final NotificationService notificationService;
//    private final BookingRepository bookingRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendEmailWithTemplate(String to, String subject, String content, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);

            // Đọc template từ thư mục resources/templates
            String template = new String(
                    new ClassPathResource(filePath).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            String htmlContent = template.replace("%s", content);

            String textContent = "Simple text version without special characters";

            helper.setText(textContent, htmlContent);
            mailSender.send(message);

            System.out.println("✅ Email sent successfully to: " + to);

        } catch (MessagingException | IOException e) {
            logger.error(e.getMessage());

        }
    }

    public void sendEmailWithTemplate(String to, String subject, String filePath, Map<String, String> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);

            // 1️⃣ Đọc file template HTML
            String template = new String(
                    new ClassPathResource(filePath).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            // 2️⃣ Thay thế tất cả placeholder trong template
            // Hỗ trợ cả %s, %CONFIRM_URL%, %USERNAME%, v.v.
            String htmlContent = template;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();

                // Dùng replaceAll với regex an toàn, xử lý cả xuống dòng
                htmlContent = htmlContent.replace("%" + key + "%", value);
            }

            // 3️⃣ Gửi email với HTML
            helper.setText("Vui lòng xem nội dung email ở định dạng HTML", htmlContent);
            mailSender.send(message);

            System.out.println("✅ Email sent successfully to: " + to);

        } catch (Exception e) {
            logger.error("Email error: {}", e.getMessage(), e);
        }
    }

    @Async
    public void sendEnrollmentEmail(String to, String studentName, String courseName, String className) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("duyanhcules@gmail.com");
            helper.setTo(to);
            helper.setSubject("Xác nhận đăng ký khóa học thành công - PinYin Centre");

            String htmlContent = String.format(
                "<html>" +
                "<body>" +
                "<h2>Chào %s,</h2>" +
                "<p>Chúc mừng bạn đã thanh toán thành công cho khóa học <strong>%s</strong> tại PinYin Centre!</p>" +
                "<p>Hệ thống đã tự động ghi danh bạn vào lớp học: <strong>%s</strong>.</p>" +
                "<p>Bạn có thể truy cập vào dashboard cá nhân để xem chi tiết lịch học.</p>" +
                "<br/>" +
                "<p>Trân trọng,<br/>PinYin Centre Team</p>" +
                "</body>" +
                "</html>",
                studentName, courseName, className
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Enrollment email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

//    @Async
//    public void sendEmailBookingRejected(Long bookingId, String reason, String confirmUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi thông báo hủy.");
//            return;
//        }
//
//        // 1. Format thời gian cho đẹp
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//        String timeStr = booking.getStartScheduleDate() != null ?
//                timeFmt.format(booking.getStartScheduleDate()) : "N/A";
//
//        // 2. Xử lý lý do (Nếu null thì gán mặc định)
//        String finalReason = (reason != null && !reason.trim().isEmpty())
//                ? reason
//                : "Nhà cung cấp bận hoặc trùng lịch đột xuất.";
//
//        // 3. Xây dựng nội dung HTML (Body) - Tông màu ĐỎ/CẢNH BÁO
//        String htmlBody = String.format("""
//                        <h3 style="color: #d32f2f; margin-top: 0;">⛔ Thông báo hủy lịch đặt</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Rất tiếc, yêu cầu đặt lịch của bạn tại <b>FotoNhanh</b> đã bị từ chối hoặc hủy bỏ.</p>
//
//                        <div style="background-color: #ffebee; border-left: 4px solid #ef5350; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #c62828; font-weight: bold; margin-bottom: 5px;">Lý do:</div>
//                            <div style="color: #333;">%s</div>
//                        </div>
//
//                        <p><b>Chi tiết đơn hàng:</b></p>
//                        <ul style="background-color: #f9fafb; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee; list-style-type: circle;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li style="margin-bottom: 5px;">Nhà cung cấp: %s</li>
//                            <li>Thời gian dự kiến: %s</li>
//                        </ul>
//
//                        <p style="margin-top: 20px; font-size: 14px; color: #555;">
//                            <i>* Lưu ý: Nếu bạn đã thanh toán tiền cọc, hệ thống sẽ tự động tạo giao dịch hoàn tiền về số dư của bạn tại FotoNhanh trong vòng 24h.</i>
//                        </p>
//
//                        <p>Chúng tôi rất tiếc về sự bất tiện này. Mời bạn tham khảo các gói dịch vụ khác.</p>
//                        """,
//                customer.getFullName(),
//                finalReason,
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                booking.getBusinessProfile().getName(),
//                timeStr
//        );
//
//        // 4. Map biến vào Template chung
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Tìm dịch vụ khác");
//
//        variables.put("CONFIRM_URL", confirmUrl);
//
//        // 5. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "⛔ Thông báo hủy lịch đặt #" + booking.getBookingId(),
//                "templates/general-notification.html", // Dùng lại file HTML mẫu
//                variables
//        );
//
//        logger.info("Đã gửi email từ chối booking ID {} tới: {}", booking.getBookingId(), customer.getEmail());
//    }

//    @Async
//    public void sendEmailBookingAccepted(Long bookingId, String confirmUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi xác nhận.");
//            return;
//        }
//
//        // 1. Format ngày giờ
//        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");
//
//        String dateStr = booking.getStartScheduleDate() != null ? dateFmt.format(booking.getStartScheduleDate()) : "N/A";
//        String timeRange = (booking.getStartScheduleDate() != null ? timeFmt.format(booking.getStartScheduleDate()) : "..")
//                + " - " +
//                (booking.getEndScheduleDate() != null ? timeFmt.format(booking.getEndScheduleDate()) : "..");
//
//        // 2. Tính toán tiền nong hiển thị
//        double total = booking.getTotalPrice() != null ? booking.getTotalPrice() : 0;
//        double deposit = booking.getDeposit() != null ? booking.getDeposit() : 0;
//        double remaining = Math.max(0, total - deposit);
//
//        // 3. Xây dựng nội dung HTML (Body) - Tông màu XANH LÁ
//        String htmlBody = String.format("""
//                        <h3 style="color: #2e7d32; margin-top: 0;">✅ Đặt lịch thành công!</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Tin vui! Yêu cầu đặt lịch của bạn với <b>%s</b> đã được xác nhận chính thức.</p>
//
//                        <p><b>Thông tin chi tiết:</b></p>
//
//                        <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px; font-size: 15px;">
//                            <tr style="border-bottom: 1px solid #eee;">
//                                <td style="padding: 10px 0; color: #555;">Mã đơn hàng:</td>
//                                <td style="padding: 10px 0; font-weight: bold; text-align: right;">#%s</td>
//                            </tr>
//                            <tr style="border-bottom: 1px solid #eee;">
//                                <td style="padding: 10px 0; color: #555;">Dịch vụ:</td>
//                                <td style="padding: 10px 0; font-weight: bold; text-align: right;">%s</td>
//                            </tr>
//                            <tr style="border-bottom: 1px solid #eee;">
//                                <td style="padding: 10px 0; color: #555;">Ngày chụp:</td>
//                                <td style="padding: 10px 0; font-weight: bold; text-align: right;">%s</td>
//                            </tr>
//                            <tr style="border-bottom: 1px solid #eee;">
//                                <td style="padding: 10px 0; color: #555;">Khung giờ:</td>
//                                <td style="padding: 10px 0; font-weight: bold; text-align: right;">%s</td>
//                            </tr>
//                            <tr style="border-bottom: 1px solid #eee;">
//                                <td style="padding: 10px 0; color: #555;">Địa điểm:</td>
//                                <td style="padding: 10px 0; font-weight: bold; text-align: right;">%s</td>
//                            </tr>
//                        </table>
//
//                        <div style="background-color: #f1f8e9; padding: 15px; border-radius: 8px; border: 1px solid #c5e1a5;">
//                            <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
//                                <span>Tổng giá trị:</span>
//                                <b>%,.0f đ</b>
//                            </div>
//                            <div style="display: flex; justify-content: space-between; margin-bottom: 5px; color: #2e7d32;">
//                                <span>Đã đặt cọc:</span>
//                                <b>- %,.0f đ</b>
//                            </div>
//                            <div style="border-top: 1px dashed #aed581; margin: 8px 0;"></div>
//                            <div style="display: flex; justify-content: space-between; font-size: 16px;">
//                                <span style="font-weight: bold;">Cần thanh toán thêm:</span>
//                                <span style="font-weight: bold; color: #d32f2f;">%,.0f đ</span>
//                            </div>
//                        </div>
//
//                        <p style="margin-top: 20px;">Vui lòng đến đúng giờ để buổi chụp diễn ra thuận lợi nhất. Nếu có thay đổi, hãy liên hệ ngay với nhà cung cấp.</p>
//                        """,
//                customer.getFullName(),
//                booking.getBusinessProfile().getName(),
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                dateStr,
//                timeRange,
//                booking.getAddress(),
//                total,
//                deposit,
//                remaining
//        );
//
//        // 4. Map biến vào Template chung
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Xem chi tiết đơn hàng");
//
//        // Link trỏ về trang chi tiết booking (FE)
//        // Ví dụ: http://localhost:3000/booking/detail/123
//        variables.put("CONFIRM_URL", confirmUrl);
//
//        // 5. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "✅ Xác nhận đặt lịch thành công - Đơn hàng #" + booking.getBookingId(),
//                "templates/general-notification.html",
//                variables
//        );
//
//        logger.info("✅ Đã gửi email xác nhận booking ID {} tới: {}", booking.getBookingId(), customer.getEmail());
//    }

    // --- GỬI CHO NHÀ CUNG CẤP (PROVIDER) ---

//    @Async
//    public void sendEmailPhotosDelivered(Long bookingId, String viewPhotosUrl, int version) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi thông báo trả ảnh.");
//            return;
//        }
//
//        // 1. Xác định nội dung dựa trên version
//        boolean isFirstTime = version <= 1;
//        String providerName = booking.getBusinessProfile().getName();
//
//        // Tiêu đề email
//        String subject = isFirstTime
//                ? "Ảnh của bạn đã sẵn sàng! - Đơn hàng #" + booking.getBookingId()
//                : "Cập nhật ảnh chỉnh sửa (Phiên bản " + version + ") - Đơn hàng #" + booking.getBookingId();
//
//        // Tiêu đề trong nội dung HTML
//        String headerTitle = isFirstTime ? "Trả ảnh hoàn thiện" : "Cập nhật phiên bản mới";
//
//        // Lời dẫn
//        String messageIntro = isFirstTime
//                ? "Tin vui! <b>" + providerName + "</b> đã hoàn tất quá trình chỉnh sửa và gửi toàn bộ ảnh cho bạn."
//                : "Nhà cung cấp <b>" + providerName + "</b> vừa cập nhật phiên bản chỉnh sửa mới (Phiên bản " + version + ") cho bộ ảnh của bạn theo yêu cầu.";
//
//        // Màu sắc chủ đạo (Teal cho delivery)
//        String themeColor = isFirstTime ? "#00897b" : "#f57c00"; // Xanh teal cho lần đầu, Cam cho update
//        String bgColor = isFirstTime ? "#e0f2f1" : "#fff3e0";
//        String borderColor = isFirstTime ? "#4db6ac" : "#ffb74d";
//
//        // 2. Xây dựng nội dung HTML (Body)
//        String htmlBody = String.format("""
//                        <h3 style="color: %s; margin-top: 0;">%s</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>%s</p>
//
//                        <div style="background-color: %s; padding: 20px; border-radius: 8px; border: 1px solid %s; text-align: center; margin: 20px 0;">
//                            <div style="font-size: 16px; font-weight: bold; color: #555; margin-bottom: 10px;">
//                                %s
//                            </div>
//                            <p style="margin: 0; color: #666; font-size: 14px;">
//                                Bạn có thể xem trực tuyến hoặc yêu cầu chỉnh sửa thêm (nếu gói dịch vụ cho phép).
//                            </p>
//                        </div>
//
//                        <p><b>Thông tin đơn hàng:</b></p>
//                        <ul style="background-color: #f9fafb; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li>Phiên bản hiện tại: <b>Phiên bản 5 %d</b></li>
//                        </ul>
//
//                        <p style="margin-top: 20px;">Cảm ơn bạn đã sử dụng dịch vụ tại FotoNhanh!</p>
//                        """,
//                themeColor, headerTitle,
//                customer.getFullName(),
//                messageIntro,
//                bgColor, borderColor,
//                isFirstTime ? "🎉 Bộ ảnh của bạn đã hoàn tất!" : "Đã cập nhật ảnh chỉnh sửa",
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                version
//        );
//
//        // 3. Map biến vào Template chung
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//
//        // Nút bấm
//        variables.put("BUTTON_TEXT", isFirstTime ? "Xem & Tải ảnh ngay" : "Xem bản cập nhật");
//        variables.put("CONFIRM_URL", viewPhotosUrl);
//
//        // 4. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                subject,
//                "templates/general-notification.html", // Dùng chung file template
//                variables
//        );
//
//        logger.info("✅ Đã gửi email trả ảnh (Version {}) cho booking ID {} tới: {}", version, booking.getBookingId(), customer.getEmail());
//    }

//    @Async
//    public void sendEmailDirectPaymentPending(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi thông báo chờ xác nhận thanh toán trực tiếp.");
//            return;
//        }
//
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//        String timeStr = timeFmt.format(new java.util.Date()); // Thời điểm hiện tại
//        double amount = booking.getDeposit() != null ? booking.getDeposit() : 0; // Giả sử thanh toán cọc
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #f57c00; margin-top: 0;">⏳ Đã ghi nhận yêu cầu thanh toán</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Hệ thống đã ghi nhận bạn chọn phương thức <b>Thanh toán trực tiếp</b> cho đơn hàng <b>#%s</b>.</p>
//
//                        <div style="background-color: #fff3e0; border-left: 4px solid #ffb74d; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #ef6c00; font-weight: bold; margin-bottom: 5px;">Trạng thái: Đang chờ xác nhận</div>
//                            <div style="color: #333;">Vui lòng hoàn tất thanh toán với nhà cung cấp. Sau khi nhà cung cấp xác nhận đã nhận tiền, lịch đặt của bạn sẽ được chuyển sang trạng thái chính thức.</div>
//                        </div>
//
//                        <p><b>Thông tin giao dịch:</b></p>
//                        <ul style="background-color: #f9fafb; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee; list-style-type: circle;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li style="margin-bottom: 5px;">Số tiền cần thanh toán: <b style="color: #d32f2f;">%,.0f đ</b></li>
//                            <li>Thời gian yêu cầu: %s</li>
//                        </ul>
//
//                        <p style="margin-top: 20px; font-size: 14px; color: #555;">
//                            <i>* Nếu bạn đã thanh toán nhưng chưa thấy cập nhật, vui lòng liên hệ trực tiếp với nhà cung cấp <b>%s</b>.</i>
//                        </p>
//                        """,
//                customer.getFullName(),
//                booking.getBookingId(),
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                amount,
//                timeStr,
//                booking.getBusinessProfile().getName()
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Xem chi tiết đơn hàng");
//        variables.put("CONFIRM_URL", bookingUrl);
//
//        // 4. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "⏳ Xác nhận thanh toán trực tiếp - Đơn hàng #" + booking.getBookingId(),
//                "templates/general-notification.html",
//                variables
//        );
//
//        logger.info("Đã gửi email thông báo chờ xác nhận thanh toán trực tiếp booking ID {} tới: {}", booking.getBookingId(), customer.getEmail());
//    }

//    @Async
//    public void sendEmailDirectPaymentConfirmed(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi xác nhận thanh toán thành công.");
//            return;
//        }
//
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//        String timeStr = timeFmt.format(new java.util.Date());
//        double amount = booking.getDeposit() != null ? booking.getDeposit() : 0;
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #1565c0; margin-top: 0;">💰 Xác nhận thanh toán thành công</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Nhà cung cấp <b>%s</b> đã xác nhận nhận được khoản thanh toán trực tiếp của bạn cho đơn hàng <b>#%s</b>.</p>
//
//                        <div style="background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #0d47a1; font-weight: bold; margin-bottom: 5px;">Trạng thái: Đã thanh toán</div>
//                            <div style="color: #333;">Lịch đặt của bạn hiện đã được đảm bảo. Hãy chuẩn bị sẵn sàng cho buổi chụp nhé!</div>
//                        </div>
//
//                        <p><b>Chi tiết thanh toán:</b></p>
//                        <ul style="background-color: #f9fafb; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee; list-style-type: circle;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li style="margin-bottom: 5px;">Số tiền đã thanh toán: <b style="color: #2e7d32;">%,.0f đ</b></li>
//                            <li>Thời gian xác nhận: %s</li>
//                        </ul>
//
//                        <p>Cảm ơn bạn đã tin tưởng sử dụng dịch vụ tại FotoNhanh!</p>
//                        """,
//                customer.getFullName(),
//                booking.getBusinessProfile().getName(),
//                booking.getBookingId(),
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                amount,
//                timeStr
//        );
//
//        // 3. Map biến vào Template chung
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Kiểm tra lịch đặt");
//        variables.put("CONFIRM_URL", bookingUrl);
//
//        // 4. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "💰 Xác nhận thanh toán thành công - Đơn hàng #" + booking.getBookingId(),
//                "templates/general-notification.html",
//                variables
//        );
//
//        logger.info("Đã gửi email xác nhận thanh toán trực tiếp thành công booking ID {} tới: {}", booking.getBookingId(), customer.getEmail());
//    }


    // --- GỬI CHO NHÀ CUNG CẤP (PROVIDER) ---

//    @Async
//    public void sendEmailToProviderPaymentPending(Long bookingId, String manageUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        // Giả định BusinessProfile có liên kết với User (chủ shop)
//        UserEntity provider = booking.getBusinessProfile().getUser();
//        UserEntity customer = booking.getCustomer();
//
//        if (provider == null || provider.getEmail() == null) return;
//
//        double amount = booking.getDeposit() != null ? booking.getDeposit() : 0;
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #e65100; margin-top: 0;">💸 Yêu cầu thanh toán mới</h3>
//                        <p>Xin chào đối tác <b>%s</b>,</p>
//                        <p>Khách hàng <b>%s</b> vừa chọn phương thức <b>Thanh toán trực tiếp</b> cho đơn hàng <b>#%s</b>.</p>
//
//                        <div style="background-color: #fff3e0; border: 1px solid #ffe0b2; padding: 15px; border-radius: 4px;">
//                            <p style="margin: 0; color: #ef6c00; font-weight: bold;">Hành động cần thiết:</p>
//                            <p style="margin: 5px 0 0 0;">Vui lòng thu tiền từ khách hàng và bấm nút <b>"Xác nhận đã nhận tiền"</b> trên hệ thống quản lý.</p>
//                        </div>
//
//                        <ul style="background-color: #f5f5f5; padding: 15px 15px 15px 35px; border-radius: 8px;">
//                            <li>Số tiền cần thu: <b style="color: #d32f2f;">%,.0f đ</b></li>
//                            <li>Khách hàng: %s</li>
//                            <li>SĐT khách hàng: %s</li>
//                        </ul>
//                        """,
//                provider.getFullName(),
//                customer.getFullName(),
//                booking.getBookingId(),
//                amount,
//                customer.getFullName(),
//                booking.getCustomer().getPhone()
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Quản lý đơn hàng");
//        variables.put("CONFIRM_URL", manageUrl);
//
//        sendEmailWithTemplate(provider.getEmail(), "💸 Khách hàng muốn thanh toán trực tiếp - #" + bookingId, "templates/general-notification.html", variables);
//    }

//    @Async
//    public void sendEmailToProviderPaymentConfirmed(Long bookingId, String manageUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity provider = booking.getBusinessProfile().getUser();
//        UserEntity customer = booking.getCustomer();
//
//        if (provider == null || provider.getEmail() == null) return;
//
//        double amount = booking.getDeposit() != null ? booking.getDeposit() : 0;
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #2e7d32; margin-top: 0;">💰 Thu tiền thành công</h3>
//                        <p>Xin chào đối tác <b>%s</b>,</p>
//                        <p>Bạn đã xác nhận nhận tiền thành công từ khách hàng <b>%s</b> cho đơn hàng <b>#%s</b>.</p>
//
//                        <div style="background-color: #e8f5e9; border: 1px solid #c8e6c9; padding: 15px; border-radius: 4px;">
//                             <p style="margin: 0; color: #2e7d32;">Trạng thái đơn hàng đã được cập nhật. Hãy chuẩn bị phục vụ khách hàng tốt nhất nhé!</p>
//                        </div>
//
//                        <ul style="background-color: #f5f5f5; padding: 15px 15px 15px 35px; border-radius: 8px;">
//                            <li>Số tiền đã thu: <b>%,.0f đ</b></li>
//                            <li>Thời gian xác nhận: %s</li>
//                        </ul>
//                        """,
//                provider.getFullName(),
//                customer.getFullName(),
//                booking.getBookingId(),
//                amount,
//                new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date())
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Xem đơn hàng");
//        variables.put("CONFIRM_URL", manageUrl);
//
//        sendEmailWithTemplate(provider.getEmail(), "💰 Xác nhận thu tiền thành công - #" + bookingId, "templates/general-notification.html", variables);
//    }

//    @Async
//    @Transactional
//    public void sendNotificationAndEmail(Long bookingId, boolean isAccepted, String bookingPageUrl) {
//        try {
//            String title = isAccepted ? "Lịch đặt chụp ảnh của bạn đã được xác nhận" : "Lịch đặt chụp ảnh đã bị hủy";
//            String message = isAccepted ? "Lịch đặt chụp của bạn đã được xác nhận, kiểm tra ngay!" : "Bên cung cấp từ chối lịch chụp do trùng lịch.";
//
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//            // Tạo notification (Nên để async nốt nếu notificationService chậm)
//            CreateNotificationDto dto = CreateNotificationDto.builder()
//                    .userId(booking.getCustomer().getId())
//                    .title(title)
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message(message)
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build();
//
//            notificationService.createNotification(dto);
//
//            if (isAccepted) {
//                sendEmailBookingAccepted(booking.getBookingId(), bookingPageUrl);
//            } else {
//                sendEmailBookingRejected(booking.getBookingId(), "Bên cung cấp từ chối lịch chụp", bookingPageUrl);
//            }
//        } catch (Exception e) {
//            throw new BusinessException("Lỗi khi gửi thông báo!");
//        }
//
//    }


    /**
     * Hàm bao đóng: Gửi 2 CHIỀU (Khách + Chủ shop) khi yêu cầu thanh toán (Pending)
     */
//    @Async
//    @Transactional
//    public void sendNotificationAndEmailPaymentPending(Long bookingId, String bookingPageUrl, String providerManageUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//
//            UserEntity customer = booking.getCustomer();
//            UserEntity provider = booking.getBusinessProfile().getUser(); // Lấy chủ shop
//
//            // --- 1. GỬI CHO KHÁCH HÀNG ---
//            // 1.1 Notify Khách
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(customer.getId())
//                    .title("Yêu cầu thanh toán đang chờ")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Yêu cầu thanh toán trực tiếp cho đơn #" + booking.getBookingId() + " đã được ghi nhận.")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//
//            // 1.2 Email Khách
//            sendEmailDirectPaymentPending(bookingId, bookingPageUrl);
//
//
//            // --- 2. GỬI CHO NHÀ CUNG CẤP ---
//            if (provider != null) {
//                // 2.1 Notify Chủ shop
//                notificationService.createNotification(CreateNotificationDto.builder()
//                        .userId(provider.getId())
//                        .title("💸 Có yêu cầu thanh toán mới")
//                        .type(NotificationEntity.NotificationType.BOOKING) // Hoặc PAYMENT
//                        .message("Khách " + customer.getFullName() + " muốn thanh toán trực tiếp đơn #" + booking.getBookingId())
//                        .referenceId(booking.getBookingId())
//                        .targetUrl("/booking/view") // URL quản lý của chủ shop
//                        .build());
//
//                // 2.2 Email Chủ shop
//                sendEmailToProviderPaymentPending(bookingId, providerManageUrl);
//            }
//
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi thông báo 2 chiều Payment Pending: {}", e.getMessage());
//            // Không throw exception để tránh rollback giao dịch chính nếu chỉ lỗi gửi mail
//        }
//    }

    /**
     * Hàm bao đóng: Gửi 2 CHIỀU (Khách + Chủ shop) khi xác nhận đã nhận tiền (Confirmed)
     */
//    @Async
//    @Transactional
//    public void sendNotificationAndEmailPaymentConfirmed(Long bookingId, String bookingPageUrl, String providerManageUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//
//            UserEntity customer = booking.getCustomer();
//            UserEntity provider = booking.getBusinessProfile().getUser();
//
//            // --- 1. GỬI CHO KHÁCH HÀNG ---
//            // 1.1 Notify Khách
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(customer.getId())
//                    .title("Thanh toán thành công")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Nhà cung cấp đã xác nhận nhận tiền cho đơn hàng #" + booking.getBookingId() + ".")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//
//            // 1.2 Email Khách
//            sendEmailDirectPaymentConfirmed(bookingId, bookingPageUrl);
//
//
//            // --- 2. GỬI CHO NHÀ CUNG CẤP ---
//            if (provider != null) {
//                // 2.1 Notify Chủ shop
//                notificationService.createNotification(CreateNotificationDto.builder()
//                        .userId(provider.getId())
//                        .title("💰 Thu tiền thành công")
//                        .type(NotificationEntity.NotificationType.BOOKING)
//                        .message("Bạn đã xác nhận thu tiền đơn #" + booking.getBookingId() + " thành công.")
//                        .referenceId(booking.getBookingId())
//                        .targetUrl("/booking/view")
//                        .build());
//
//                // 2.2 Email Chủ shop
//                sendEmailToProviderPaymentConfirmed(bookingId, providerManageUrl);
//            }
//
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi thông báo 2 chiều Payment Confirmed: {}", e.getMessage());
//        }
//    }

//    @Async
//    @Transactional
//    public void sendNotificationAndEmailPhotosDelivered(Long bookingId, String viewPhotosUrl, int version) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//
//            boolean isFirstTime = version <= 1;
//            String title = isFirstTime ? "Ảnh của bạn đã sẵn sàng!" : "Có bản cập nhật ảnh mới";
//            String message = isFirstTime
//                    ? "Nhà cung cấp đã trả ảnh hoàn thiện cho đơn hàng #" + booking.getBookingId()
//                    : "Đã có phiên bản chỉnh sửa số " + version + " cho đơn hàng #" + booking.getBookingId();
//
//            // 1. Tạo Notification
//            CreateNotificationDto dto = CreateNotificationDto.builder()
//                    .userId(booking.getCustomer().getId())
//                    .title(title)
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message(message)
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build();
//
//            notificationService.createNotification(dto);
//
//            // 2. Gửi Email
//            sendEmailPhotosDelivered(bookingId, viewPhotosUrl, version);
//
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi thông báo Photos Delivered: {}", e.getMessage());
//            throw new BusinessException("Lỗi khi gửi thông báo trả ảnh!");
//        }
//    }

    // --- GỬI EMAIL KHI CHỤP XONG (SHOOT DONE) ---

//    @Async
//    public void sendEmailToCustomerShootDone(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        UserEntity provider = booking.getBusinessProfile().getUser();
//
//        if (customer == null || customer.getEmail() == null) return;
//
//        String providerName = booking.getBusinessProfile().getName();
//
//        // Tông màu: Xanh dương nhạt (Hy vọng/Chờ đợi)
//        String htmlBody = String.format("""
//                        <h3 style="color: #0288d1; margin-top: 0;">📸 Buổi chụp đã hoàn tất!</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Cảm ơn bạn đã tham gia buổi chụp cùng <b>%s</b>. Hệ thống đã ghi nhận trạng thái đơn hàng <b>#%s</b> là <b>Đã chụp xong</b>.</p>
//
//                        <div style="background-color: #e1f5fe; border-left: 4px solid #29b6f6; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #0277bd; font-weight: bold; margin-bottom: 5px;">Bước tiếp theo: Xác nhận chụp xong</div>
//                            <div style="color: #333;">Hãy xác nhận buổi chụp đã hoàn thành để nhà cung cấp có thể trả ảnh!</div>
//                        </div>
//
//                        <p>Nếu bạn hài lòng về buổi chụp, hãy chờ đợi những bức ảnh tuyệt vời nhé!</p>
//                        """,
//                customer.getFullName(),
//                providerName,
//                booking.getBookingId()
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Xem chi tiết đơn hàng");
//        variables.put("CONFIRM_URL", bookingUrl);
//
//        sendEmailWithTemplate(customer.getEmail(), "📸 Buổi chụp đã hoàn tất - #" + bookingId, "templates/general-notification.html", variables);
//    }

//    @Async
//    public void sendEmailToProviderShootDone(Long bookingId, String manageUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity provider = booking.getBusinessProfile().getUser();
//        UserEntity customer = booking.getCustomer();
//
//        if (provider == null || provider.getEmail() == null) return;
//
//        // Tông màu: Tím (Sáng tạo/Hậu kỳ)
//        String htmlBody = String.format("""
//                        <h3 style="color: #7b1fa2; margin-top: 0;">🎬 Xác nhận hoàn thành chụp</h3>
//                        <p>Xin chào đối tác <b>%s</b>,</p>
//                        <p>Bạn đã đánh dấu đơn hàng <b>#%s</b> của khách hàng <b>%s</b> là <b>Đã chụp xong</b>.</p>
//
//                        <div style="background-color: #f3e5f5; border: 1px solid #e1bee7; padding: 15px; border-radius: 4px;">
//                             <p style="margin: 0; color: #6a1b9a; font-weight: bold;">Hành động tiếp theo:</p>
//                             <p style="margin: 5px 0 0 0;">Vui lòng liên hệ với khách hàng để xác nhận hoàn thành buổi chụp</p>
//                        </div>
//
//                        <p>Chúc bạn có những tác phẩm ưng ý!</p>
//                        """,
//                provider.getFullName(),
//                booking.getBookingId(),
//                customer.getFullName()
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Tải ảnh lên");
//        variables.put("CONFIRM_URL", manageUrl); // Link đến trang quản lý để upload ảnh
//
//        sendEmailWithTemplate(provider.getEmail(), "🎬 Đã chụp xong - Vui lòng yêu cầu khách hàng xác nhận hoàn thành#" + bookingId, "templates/general-notification.html", variables);
//    }

    /**
     * Hàm bao đóng: Gửi 2 CHIỀU (Khách + Chủ shop) khi Đã chụp xong (Shoot Done)
     */
//    @Async
//    @Transactional
//    public void sendNotificationAndEmailShootDone(Long bookingId, String customerUrl, String providerUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//
//            UserEntity customer = booking.getCustomer();
//            UserEntity provider = booking.getBusinessProfile().getUser();
//
//            // --- 1. GỬI CHO KHÁCH HÀNG ---
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(customer.getId())
//                    .title("Buổi chụp đã hoàn tất")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Buổi chụp cho đơn hàng #" + booking.getBookingId() + " đã kết thúc. Vui lòng xác nhận.")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//
//            sendEmailToCustomerShootDone(bookingId, customerUrl);
//
//
//            // --- 2. GỬI CHO NHÀ CUNG CẤP ---
//            if (provider != null) {
//                notificationService.createNotification(CreateNotificationDto.builder()
//                        .userId(provider.getId())
//                        .title("Đã hoàn thành chụp")
//                        .type(NotificationEntity.NotificationType.BOOKING)
//                        .message("Đã cập nhật trạng thái đơn #" + booking.getBookingId() + ". Hãy yêu cầu khách hàng xác nhận ngay nhé!")
//                        .referenceId(booking.getBookingId())
//                        .targetUrl("/owner?tab=booking")
//                        .build());
//
//                sendEmailToProviderShootDone(bookingId, providerUrl);
//            }
//
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi thông báo 2 chiều Shoot Done: {}", e.getMessage());
//        }
//    }


    // --- CASE 1: CHẤP THUẬN YÊU CẦU HỦY CỦA KHÁCH ---
//    @Async
//    public void sendEmailCancellationRequestAccepted(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        if (customer == null || customer.getEmail() == null) return;
//
//        double refundAmount = booking.getDeposit() != null ? booking.getDeposit() : 0;
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #43a047; margin-top: 0;">✅ Đã chấp thuận hủy lịch</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Yêu cầu hủy đơn hàng <b>#%s</b> của bạn đã được nhà cung cấp chấp thuận.</p>
//
//                        <div style="background-color: #e8f5e9; border-left: 4px solid #66bb6a; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #2e7d32; font-weight: bold;">Hoàn tiền cọc:</div>
//                            <div style="color: #333;">Số tiền <b>%,.0f đ</b> đã được hoàn lại vào ví của bạn trên hệ thống.</div>
//                        </div>
//
//                        <p>Hy vọng sẽ được phục vụ bạn trong các dịp khác!</p>
//                        """,
//                customer.getFullName(),
//                booking.getBookingId(),
//                refundAmount
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Kiểm tra ví");
//        variables.put("CONFIRM_URL", bookingUrl); // Link trỏ về trang ví hoặc chi tiết đơn
//
//        sendEmailWithTemplate(customer.getEmail(), "✅ Đã chấp thuận hủy lịch - #" + bookingId, "templates/general-notification.html", variables);
//    }

    // --- CASE 3: STUDIO HỦY LỊCH ĐÃ CONFIRM (SỰ CỐ) ---
//    @Async
//    public void sendEmailStudioCancelledConfirmed(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        String providerName = booking.getBusinessProfile().getName();
//        if (customer == null || customer.getEmail() == null) return;
//
//        double refundAmount = booking.getDeposit() != null ? booking.getDeposit() : 0;
//
//        String htmlBody = String.format("""
//                        <h3 style="color: #c62828; margin-top: 0;">⚠️ Thông báo hủy lịch đột xuất</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Chúng tôi rất tiếc phải thông báo: Lịch đặt <b>#%s</b> của bạn với <b>%s</b> đã bị hủy bỏ.</p>
//
//
//                        <div style="background-color: #e3f2fd; padding: 15px; border-radius: 4px; margin-top: 15px;">
//                            <p style="margin: 0; color: #0d47a1; font-weight: bold;">Thông tin hoàn tiền:</p>
//                            <p style="margin: 5px 0 0 0;">Hệ thống đã tự động hoàn lại tiền cọc <b>%,.0f đ</b> vào ví của bạn.</p>
//                        </div>
//
//                        <p>Thành thật xin lỗi vì sự bất tiện này.</p>
//                        """,
//                customer.getFullName(),
//                booking.getBookingId(),
//                providerName,
//                refundAmount
//        );
//
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Tìm dịch vụ khác");
//        variables.put("CONFIRM_URL", bookingUrl); // Link trỏ về trang tìm kiếm hoặc chi tiết đơn
//
//        sendEmailWithTemplate(customer.getEmail(), "⚠️ Thông báo hủy lịch - #" + bookingId, "templates/general-notification.html", variables);
//    }

    // Wrapper cho CASE 1
//    @Async
//    @Transactional
//    public void sendNotificationAndEmailCancellationAccepted(Long bookingId, String customerUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow();
//            // 1. Notify
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(booking.getCustomer().getId())
//                    .title("Đã chấp thuận hủy lịch")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Yêu cầu hủy đơn #" + booking.getBookingId() + " đã được chấp nhận. Tiền cọc đã được hoàn.")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//            // 2. Email
//            sendEmailCancellationRequestAccepted(bookingId, customerUrl);
//        } catch (Exception e) {
//            logger.error("Lỗi gửi thông báo Case 1: " + e.getMessage());
//        }
//    }

    // Wrapper cho CASE 3
//    @Async
//    @Transactional
//    public void sendNotificationAndEmailStudioCancelled(Long bookingId, String customerUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow();
//            // 1. Notify
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(booking.getCustomer().getId())
//                    .title("Lịch đặt bị hủy")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Đơn #" + booking.getBookingId() + " bị hủy bởi nhà cung cấp.")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//            // 2. Email
//            sendEmailStudioCancelledConfirmed(bookingId, customerUrl);
//        } catch (Exception e) {
//            logger.error("Lỗi gửi thông báo Case 3: " + e.getMessage());
//        }
//    }

//    @Async
//    @Transactional
//    public void sendEmailBookingExpired(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi thông báo hủy do hết hạn.");
//            return;
//        }
//
//        // 1. Format thời gian
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//        String createdTime = booking.getCreatedAt() != null ? timeFmt.format(booking.getCreatedAt()) : "N/A";
//        String expiredTime = timeFmt.format(new java.util.Date());
//
//        // 2. Nội dung HTML - Tông màu XÁM/ĐEN (Hết hiệu lực)
//        String htmlBody = String.format("""
//                        <h3 style="color: #616161; margin-top: 0;">⏳ Đơn hàng đã hết hạn</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Hệ thống thông báo: Yêu cầu đặt lịch <b>#%s</b> của bạn đã tự động bị hủy do quá thời hạn xử lý.</p>
//
//                        <div style="background-color: #f5f5f5; border-left: 4px solid #9e9e9e; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #424242; font-weight: bold; margin-bottom: 5px;">Nguyên nhân:</div>
//                            <div style="color: #333;">Đơn đặt lịch quá thời gian xác nhận.</div>
//                        </div>
//
//                        <p><b>Thông tin đơn hàng đã hủy:</b></p>
//                        <ul style="background-color: #fafafa; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li style="margin-bottom: 5px;">Thời gian tạo: %s</li>
//                            <li>Thời gian hủy: %s</li>
//                        </ul>
//
//                        <p style="margin-top: 20px;">Nếu bạn vẫn muốn sử dụng dịch vụ, vui lòng tạo một yêu cầu đặt lịch mới.</p>
//                        """,
//                customer.getFullName(),
//                booking.getBookingId(),
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                createdTime,
//                expiredTime
//        );
//
//        // 3. Map biến vào Template
//        Map<String, String> variables = new HashMap<>();
//        variables.put("CONTENT", htmlBody);
//        variables.put("BUTTON_TEXT", "Đặt lại dịch vụ");
//        variables.put("CONFIRM_URL", bookingUrl); // Dẫn về trang chi tiết gói dịch vụ hoặc trang chủ
//
//        // 4. Gửi mail
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "⏳ Thông báo hủy đơn hàng #" + booking.getBookingId() + " do hết hạn",
//                "templates/general-notification.html",
//                variables
//        );
//
//        logger.info("Đã gửi email thông báo hủy do hết hạn booking ID {} tới: {}", booking.getBookingId(), customer.getEmail());
//    }

//    public void sendEmailShootDoneConfirmed(Long bookingId, String bookingUrl) {
//        BookingEntity booking = bookingRepository.getFirstByBookingId(bookingId);
//        UserEntity customer = booking.getCustomer();
//        UserEntity provider = booking.getBusinessProfile().getUser(); // Assuming BusinessProfile has a User
//
//        if (customer == null || customer.getEmail() == null) {
//            logger.warn("⚠️ Không tìm thấy email khách hàng để gửi xác nhận hoàn thành buổi chụp.");
//            return;
//        }
//
//        String providerName = booking.getBusinessProfile().getName();
//        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//        String confirmTime = timeFmt.format(new java.util.Date());
//
//        String htmlBodyCustomer = String.format("""
//                        <h3 style="color: #2e7d32; margin-top: 0;">✅ Xác nhận hoàn thành buổi chụp</h3>
//                        <p>Xin chào <b>%s</b>,</p>
//                        <p>Bạn đã xác nhận hoàn thành buổi chụp cho đơn hàng <b>#%s</b> với nhà cung cấp <b>%s</b>.</p>
//
//                        <div style="background-color: #e8f5e9; border-left: 4px solid #4caf50; padding: 15px; margin: 20px 0; border-radius: 4px;">
//                            <div style="color: #2e7d32; font-weight: bold; margin-bottom: 5px;">Trạng thái: Chờ trả ảnh</div>
//                            <div style="color: #333;">Nhà cung cấp sẽ tiến hành chỉnh sửa và gửi ảnh cho bạn trong thời gian sớm nhất.</div>
//                        </div>
//
//                        <p><b>Thông tin xác nhận:</b></p>
//                        <ul style="background-color: #f9fafb; padding: 15px 15px 15px 35px; border-radius: 8px; border: 1px solid #eee;">
//                            <li style="margin-bottom: 5px;">Mã đơn: <b>#%s</b></li>
//                            <li style="margin-bottom: 5px;">Dịch vụ: <b>%s</b></li>
//                            <li>Thời gian xác nhận: %s</li>
//                        </ul>
//
//                        <p style="margin-top: 20px;">Cảm ơn bạn đã đồng hành cùng FotoNhanh!</p>
//                        """,
//                customer.getFullName(),
//                booking.getBookingId(),
//                providerName,
//                booking.getBookingId(),
//                booking.getServicePackage().getName(),
//                confirmTime
//        );
//
//        Map<String, String> variablesCustomer = new HashMap<>();
//        variablesCustomer.put("CONTENT", htmlBodyCustomer);
//        variablesCustomer.put("BUTTON_TEXT", "Xem chi tiết đơn hàng");
//        variablesCustomer.put("CONFIRM_URL", bookingUrl);
//
//        sendEmailWithTemplate(
//                customer.getEmail(),
//                "✅ Xác nhận hoàn thành buổi chụp - #" + booking.getBookingId(),
//                "templates/general-notification.html",
//                variablesCustomer
//        );
//
//        if (provider != null && provider.getEmail() != null) {
//            String htmlBodyProvider = String.format("""
//                        <h3 style="color: #1565c0; margin-top: 0;">📸 Khách hàng đã xác nhận xong</h3>
//                        <p>Xin chào đối tác <b>%s</b>,</p>
//                        <p>Khách hàng <b>%s</b> đã xác nhận hoàn thành buổi chụp cho đơn hàng <b>#%s</b>.</p>
//
//                        <div style="background-color: #e3f2fd; border: 1px solid #90caf9; padding: 15px; border-radius: 4px;">
//                             <p style="margin: 0; color: #0d47a1; font-weight: bold;">Bước tiếp theo:</p>
//                             <p style="margin: 5px 0 0 0;">Vui lòng tiến hành chỉnh sửa và trả ảnh cho khách hàng đúng hạn cam kết.</p>
//                        </div>
//
//                        <ul style="background-color: #f5f5f5; padding: 15px 15px 15px 35px; border-radius: 8px; margin-top: 20px;">
//                            <li>Mã đơn: <b>#%s</b></li>
//                            <li>Khách hàng: %s</li>
//                            <li>Thời gian xác nhận: %s</li>
//                        </ul>
//                        """,
//                    provider.getFullName(),
//                    customer.getFullName(),
//                    booking.getBookingId(),
//                    booking.getBookingId(),
//                    customer.getFullName(),
//                    confirmTime
//            );
//
//            Map<String, String> variablesProvider = new HashMap<>();
//            variablesProvider.put("CONTENT", htmlBodyProvider);
//            variablesProvider.put("BUTTON_TEXT", "Quản lý đơn hàng");
//            variablesProvider.put("CONFIRM_URL", bookingUrl.replace("booking/view", "provider/booking/manage")); // Ví dụ URL provider
//
//            sendEmailWithTemplate(
//                    provider.getEmail(),
//                    "📸 Khách hàng xác nhận hoàn thành chụp - #" + booking.getBookingId(),
//                    "templates/general-notification.html",
//                    variablesProvider
//            );
//        }
//
//        logger.info("Đã gửi email xác nhận shoot done confirmed cho booking ID {}", booking.getBookingId());
//    }

//    @Async
//    @Transactional
//    public void sendNotificationAndEmailShootDoneConfirmed(Long bookingId, String bookingUrl) {
//        try {
//            BookingEntity booking = bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new BusinessException("Không tìm thấy booking: " + bookingId));
//
//            UserEntity customer = booking.getCustomer();
//            UserEntity provider = booking.getBusinessProfile().getUser();
//
//            // 1. Notify Customer
//            notificationService.createNotification(CreateNotificationDto.builder()
//                    .userId(customer.getId())
//                    .title("Đã xác nhận hoàn thành")
//                    .type(NotificationEntity.NotificationType.BOOKING)
//                    .message("Bạn đã xác nhận hoàn thành buổi chụp đơn #" + booking.getBookingId() + ". Vui lòng chờ ảnh.")
//                    .referenceId(booking.getBookingId())
//                    .targetUrl("/booking/view")
//                    .build());
//
//            // 2. Notify Provider
//            if (provider != null) {
//                notificationService.createNotification(CreateNotificationDto.builder()
//                        .userId(provider.getId())
//                        .title("Khách đã xác nhận chụp xong")
//                        .type(NotificationEntity.NotificationType.BOOKING)
//                        .message("Khách hàng đơn #" + booking.getBookingId() + " đã xác nhận hoàn thành buổi chụp.")
//                        .referenceId(booking.getBookingId())
//                        .targetUrl("/owner?tab=booking")
//                        .build());
//            }
//
//            // 3. Send Emails
//            sendEmailShootDoneConfirmed(bookingId, bookingUrl);
//
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi thông báo Shoot Done Confirmed: {}", e.getMessage());
//            // Log error but don't fail transaction if just notification/email fails
//        }
//    }

    @Async
    public void sendEmailNewServicePendingToAdmin(String adminEmail, String adminName, Long packageId, String packageName) {
        String htmlBody = String.format("""
            <h3 style="color: #ff9800; margin-top: 0;">🔔 Gói dịch vụ mới cần duyệt</h3>
            <p>Xin chào Admin <b>%s</b>,</p>
            <p>Có một gói dịch vụ mới đang chờ phê duyệt trên hệ thống FotoNhanh.</p>
            
            <div style="background-color: #fff3e0; border-left: 4px solid #ffb74d; padding: 15px; margin: 20px 0; border-radius: 4px;">
                <div style="color: #ef6c00; font-weight: bold; margin-bottom: 5px;">Thông tin gói dịch vụ:</div>
                <div style="color: #333;">
                    <p style="margin: 5px 0;"><b>Mã gói:</b> #%s</p>
                    <p style="margin: 5px 0;"><b>Tên gói:</b> %s</p>
                </div>
            </div>
            
            <p>Vui lòng xem xét và phê duyệt gói dịch vụ này.</p>
            """,
                adminName,
                packageId,
                packageName
        );

        Map<String, String> variables = new HashMap<>();
        variables.put("CONTENT", htmlBody);
        variables.put("BUTTON_TEXT", "Xem và duyệt ngay");
        variables.put("CONFIRM_URL", "https://fotonhanh.com/admin/service-packages/pending");

        sendEmailWithTemplate(
                adminEmail,
                "🔔 Gói dịch vụ mới cần duyệt - #" + packageId,
                "templates/general-notification.html",
                variables
        );

        logger.info("Đã gửi email thông báo gói dịch vụ mới cho admin: {}", adminEmail);
    }

    @Async
    public void sendEmailServiceApproved(String ownerEmail, String ownerName, Long packageId, String packageName, String adminName) {
        String htmlBody = String.format("""
            <h3 style="color: #4caf50; margin-top: 0;">✅ Gói dịch vụ đã được phê duyệt!</h3>
            <p>Xin chào <b>%s</b>,</p>
            <p>Chúc mừng! Gói dịch vụ của bạn đã được <b>%s</b> phê duyệt và hiện đã có thể hiển thị công khai trên FotoNhanh.</p>
            
            <div style="background-color: #e8f5e9; border-left: 4px solid #66bb6a; padding: 15px; margin: 20px 0; border-radius: 4px;">
                <div style="color: #2e7d32; font-weight: bold; margin-bottom: 10px;">Thông tin gói dịch vụ:</div>
                <div style="color: #333;">
                    <p style="margin: 5px 0;"><b>Mã gói:</b> #%s</p>
                    <p style="margin: 5px 0;"><b>Tên gói:</b> %s</p>
                    <p style="margin: 5px 0;"><b>Trạng thái:</b> <span style="color: #4caf50;">✅ Đã duyệt</span></p>
                </div>
            </div>
            
            <p>Gói dịch vụ của bạn giờ đây có thể được khách hàng tìm thấy và đặt lịch. Chúc bạn kinh doanh thành công!</p>
            """,
                ownerName,
                adminName,
                packageId,
                packageName
        );

        Map<String, String> variables = new HashMap<>();
        variables.put("CONTENT", htmlBody);
        variables.put("BUTTON_TEXT", "Xem gói dịch vụ");
        variables.put("CONFIRM_URL", "https://fotonhanh.com/owner/services?id=" + packageId);

        sendEmailWithTemplate(
                ownerEmail,
                "✅ Gói dịch vụ đã được duyệt - " + packageName,
                "templates/general-notification.html",
                variables
        );

        logger.info("Đã gửi email thông báo duyệt gói dịch vụ {} cho: {}", packageId, ownerEmail);
    }

    @Async
    public void sendEmailServiceRejected(String ownerEmail, String ownerName, Long packageId, String packageName, String reason, String adminName) {
        String htmlBody = String.format("""
            <h3 style="color: #f44336; margin-top: 0;">Gói dịch vụ bị từ chối</h3>
            <p>Xin chào <b>%s</b>,</p>
            <p>Rất tiếc, gói dịch vụ của bạn đã bị <b>%s</b> từ chối và chưa thể hiển thị công khai.</p>
            
            <div style="background-color: #ffebee; border-left: 4px solid #ef5350; padding: 15px; margin: 20px 0; border-radius: 4px;">
                <div style="color: #c62828; font-weight: bold; margin-bottom: 10px;">Thông tin gói dịch vụ:</div>
                <div style="color: #333;">
                    <p style="margin: 5px 0;"><b>Mã gói:</b> #%s</p>
                    <p style="margin: 5px 0;"><b>Tên gói:</b> %s</p>
                    <p style="margin: 5px 0;"><b>Trạng thái:</b> <span style="color: #f44336;">Từ chối</span></p>
                </div>
            </div>
            
            <div style="background-color: #fff3e0; border-left: 4px solid #ffb74d; padding: 15px; margin: 20px 0; border-radius: 4px;">
                <div style="color: #ef6c00; font-weight: bold; margin-bottom: 5px;">Lý do từ chối:</div>
                <div style="color: #333; font-style: italic;">%s</div>
            </div>
            
            <p>Vui lòng chỉnh sửa gói dịch vụ theo yêu cầu và gửi lại để được xét duyệt. Nếu có thắc mắc, hãy liên hệ với bộ phận hỗ trợ.</p>
            """,
                ownerName,
                adminName,
                packageId,
                packageName,
                reason
        );

        Map<String, String> variables = new HashMap<>();
        variables.put("CONTENT", htmlBody);
        variables.put("BUTTON_TEXT", "Chỉnh sửa gói dịch vụ");
        variables.put("CONFIRM_URL", "https://fotonhanh.com/owner/services/edit/" + packageId);

        sendEmailWithTemplate(
                ownerEmail,
                "Gói dịch vụ bị từ chối - " + packageName,
                "templates/general-notification.html",
                variables
        );

        logger.info("Đã gửi email thông báo từ chối gói dịch vụ {} cho: {}", packageId, ownerEmail);
    }
}