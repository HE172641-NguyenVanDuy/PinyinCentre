package com.pinyincentre.pinyin.service.utils;

import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("securityUtil")
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public boolean isCurrentUserId(String uid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(user -> user.getId().equals(uid)).orElse(false);
    }
}
