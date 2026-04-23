package com.pinyincentre.pinyin.service.authentication;

import com.nimbusds.jose.JOSEException;
import com.pinyincentre.pinyin.dto.ChangePasswordDTO;
import com.pinyincentre.pinyin.dto.TokenResponse;
import com.pinyincentre.pinyin.dto.request.AuthenticationRequest;
import com.pinyincentre.pinyin.dto.request.IntrospectRequest;
import com.pinyincentre.pinyin.dto.request.LogoutRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.AuthenticationResponse;
import com.pinyincentre.pinyin.dto.response.IntrospectResponse;
import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;

public interface AuthenticationService {
    String register(String username, String email, String password);
    String registerByAdmin(String username, String email, String password);
    TokenResponse login(String usernameOrEmail, String password);

    TokenResponse getNewAccessToken(String refreshToken);

    void logout(String token);

    String generateGoogleAuthUrl(HttpServletRequest request);

    TokenResponse authenticateAndFetchProfile(String code, String state);


    void forgotPassword(String email);

    void resetPassword(String newPassword, String token);
    UserEntity getCurrentUserOrNull();
    UserEntity requireUser();          // throw 401 nếu null
    boolean hasRole(String roleName);

    String activeAccount(String token);

    void changePassword(ChangePasswordDTO payload, String token);

    UserResponse getUserProfile(String token);
}
