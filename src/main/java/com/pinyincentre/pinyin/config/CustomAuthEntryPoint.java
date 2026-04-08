package com.pinyincentre.pinyin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinyincentre.pinyin.dto.ResultInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class này sẽ xử lý các lỗi xác thực (AuthenticationException) bị ném ra
 * bởi Spring Security (ví dụ: token không hợp lệ, chưa đăng nhập).
 * Nó sẽ trả về định dạng ResultInfo thay vì lỗi Spring mặc định.
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    // Lấy giá trị hằng số lỗi từ GlobalExceptionHandler
    // (Bạn có thể tham chiếu trực tiếp nếu ở cùng package hoặc tạo 1 class hằng số chung)
    private static final Long STATUS_ERROR = 0L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Luôn là HTTP 401

        ResultInfo<?> errorResponse = ResultInfo.builder()
                .status(STATUS_ERROR)
                .message("Xác thực không thành công. Vui lòng đăng nhập lại: " + authException.getMessage())
                .build();

        // Dùng ObjectMapper để chuyển object thành JSON và ghi vào response
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}