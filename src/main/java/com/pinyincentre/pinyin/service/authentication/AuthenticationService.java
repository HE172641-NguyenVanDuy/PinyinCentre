package com.pinyincentre.pinyin.service.authentication;

import com.nimbusds.jose.JOSEException;
import com.pinyincentre.pinyin.dto.request.AuthenticationRequest;
import com.pinyincentre.pinyin.dto.request.IntrospectRequest;
import com.pinyincentre.pinyin.dto.request.LogoutRequest;
import com.pinyincentre.pinyin.dto.response.AuthenticationResponse;
import com.pinyincentre.pinyin.dto.response.IntrospectResponse;
import com.pinyincentre.pinyin.entity.User;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    IntrospectResponse introspect(IntrospectRequest introspectRequest);
    String buildScope(User user);
    void logout(LogoutRequest request) throws ParseException, JOSEException;
}
