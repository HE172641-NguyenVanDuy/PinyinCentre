package com.pinyincentre.pinyin.util;

import com.pinyincentre.pinyin.entity.RedisKeyProperties;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
    private final RedisKeyProperties properties;

    public RedisKeyUtil(RedisKeyProperties properties) {
        this.properties = properties;
    }

    public  String getAccessTokenKey(String username) {
        return properties.getAccess() + username;
    }

    public String getRefreshTokenKey(String username) {
        return properties.getRefresh() + username;
    }

    public String getLogoutKey(String token) {
        return properties.getLogout() + token;
    }

    public String getResetPasswordToken(String email) {
        return properties.getReset() + email;
    }
    public String getActiveAccountKey(String email) {
        return properties.getActive() + email;
    }
    public String getPaymentOutKey(String token){
        return properties.getPayment() + token;
    }

}