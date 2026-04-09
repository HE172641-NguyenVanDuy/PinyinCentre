package com.pinyincentre.pinyin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private Long expiresInRefreshToken;
    private List<String> roles;
    private String username;

    public TokenResponse(String accessToken, String refreshToken, long expiresIn, long expiresInRefreshToken, List<String> roles, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.expiresInRefreshToken = expiresInRefreshToken;
        this.tokenType = "Bearer";
        this.roles = roles;
        this.username = username;
    }
}
