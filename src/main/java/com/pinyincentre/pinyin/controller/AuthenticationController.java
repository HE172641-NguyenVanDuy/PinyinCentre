package com.pinyincentre.pinyin.controller;

import com.nimbusds.jose.JOSEException;
import com.pinyincentre.pinyin.constant.AuthMessage;
import com.pinyincentre.pinyin.dto.*;
import com.pinyincentre.pinyin.dto.request.AuthenticationRequest;
import com.pinyincentre.pinyin.dto.request.IntrospectRequest;
import com.pinyincentre.pinyin.dto.request.LogoutRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.AuthenticationResponse;
import com.pinyincentre.pinyin.dto.response.IntrospectResponse;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.exception.BusinessException;
import com.pinyincentre.pinyin.service.authentication.AuthenticationService;
import com.pinyincentre.pinyin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

//    @PostMapping("/forgot-password")
//    public ResultInfo<?> forgotPassword(@RequestBody ForgotPasswordDto payload) {
//        authService.forgotPassword(payload.getEmail());
//        return ResultInfo.builder()
//                .status(ResultInfo.RESULT_OK)
//                .message(EmailEnum.SEND_EMAIL_SUCCESSFULLY.getMessage())
//                .build();s
//    }

//    @PostMapping("/register")
//    public ResultInfo<?> register(@RequestBody RegisterRequestDto request) {
//        String msg = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
//        return ResultInfo.builder()
//                .status(ResultInfo.RESULT_OK)
//                .message(msg)
//                .build();
//    }

    @PutMapping("/active")
    public ResultInfo<?> activeAccount(@RequestBody ActiveAccountDTO request) {
        String msg = authService.activeAccount(request.getToken());
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .message(msg)
                .build();
    }

    @PostMapping("/login")
    public ResultInfo<?> login(@RequestBody LoginRequestDto request) {
        TokenResponse token = authService.login(request.getUsernameOrEmail(), request.getPassword());
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .data(token)
                .build();
    }

    @GetMapping("/login-google")
    public ResultInfo<?> loginWithGoogle(HttpServletRequest request) throws IOException {
        String resultUrl = authService.generateGoogleAuthUrl(request);
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .data(resultUrl)
                .build();
    }

    @GetMapping("/callback")
    public ResultInfo<?> googleCallBack(@RequestParam(value = "state", required = false) String state, @RequestParam("code") String code) {
        log.info("Received Google callback - Code: {}, State: {}", code, state);
        TokenResponse result = authService.authenticateAndFetchProfile(code, state);
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .data(result)
                .build();
    }

    @PostMapping("/get-new-access-token")
    public ResultInfo<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String refreshToken = token.replace("Bearer ", "");

            TokenResponse newTokens = authService.getNewAccessToken(refreshToken);
            return ResultInfo.builder()
                    .status(ResultInfo.RESULT_OK)
                    .message(AuthMessage.GET_NEW_TOKEN_SUCCESSFULLY.getMessage())
                    .data(newTokens)
                    .build();
        } catch (Exception e) {
            throw new BusinessException(AuthMessage.GET_NEW_TOKEN_FAILED.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResultInfo<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .message(AuthMessage.LOGOUT_SUCCESSFULLY.getMessage())
                .build();
    }

    @PostMapping("/reset-password")
    public ResultInfo<?> resetPassword(@RequestBody ResetPasswordDto request) {
        authService.resetPassword(request.getPassword(), request.getToken());
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .message(AuthMessage.CHANGE_PASSWORD_SUCCESSFULLY.getMessage())
                .build();
    }

    @PutMapping("/change-password")
    public ResultInfo<?> changePassword(@RequestBody ChangePasswordDTO payload,
                                        @RequestHeader("Authorization") String token) {
        authService.changePassword(payload, token);
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .message(AuthMessage.CHANGE_PASSWORD_SUCCESSFULLY.getMessage())
                .build();
    }

    @GetMapping("/validate")
    public ResultInfo<?> validateToken(
            @RequestHeader("Authorization") String token) {

        if (!jwtUtil.validateTokenStrict(token)) {
            return ResultInfo.builder()
                    .status(ResultInfo.UNAUTHENTICATED)
                    .message(AuthMessage.TOKEN_INVALID.getMessage())
                    .build();
        }

        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .message(AuthMessage.VALID_TOKEN.getMessage())
                .build();
    }

    @GetMapping("/me")
    public ResultInfo<?> getUserProfile(@RequestHeader("Authorization") String token) {
        UserResponse userResponse = authService.getUserProfile(token);
        return ResultInfo.builder()
                .status(ResultInfo.RESULT_OK)
                .data(userResponse)
                .message(AuthMessage.USERNAME_EXISTS.getMessage())
                .build();
    }

}
