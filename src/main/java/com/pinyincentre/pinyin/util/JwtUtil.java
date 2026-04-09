package com.pinyincentre.pinyin.util;

import com.pinyincentre.pinyin.constant.AuthMessage;
import com.pinyincentre.pinyin.dto.TokenResponse;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.exception.AuthException;
import com.pinyincentre.pinyin.exception.BusinessException;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Data
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}") // 1 hour default
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}") // 7 days default
    private long refreshTokenExpiration;

    private final UserRepository userRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
//    private final BusinessProfileImageRepository businessProfileImageRepository;

    private Key getSigningKey() {
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String generateActiveAccount(String email, Long time) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + time * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //generate token forgot password
    public String generateResetToken(String email, Long time) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + time * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // Generate Access Token
    public String generateAccessToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenForPayout(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() ))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenResponse generateTokens(UserEntity userEntity) {
        String username = userEntity.getUsername(); // Lấy username từ đối tượng user

        String accessKey = redisKeyUtil.getAccessTokenKey(username);
        String refreshKey = redisKeyUtil.getRefreshTokenKey(username);

        // kiểm tra trong cache trước
        String cachedAccess = (String) redisTemplate.opsForValue().get(accessKey);
        String cachedRefresh = (String) redisTemplate.opsForValue().get(refreshKey);

        if (cachedAccess != null && cachedRefresh != null) {
            logger.info("[CACHE HIT] Dùng lại token từ cache cho user: {}", username);
            return new TokenResponse(cachedAccess,
                    cachedRefresh,
                    accessTokenExpiration / 1000,
                    refreshTokenExpiration / 1000,
                    extractRoles(cachedAccess),
                    username);
        }

        logger.info("[CACHE MISS] Tạo token mới cho user: {}", username);

        // Không cần tìm lại user trong DB nữa vì đã được truyền vào
//        BusinessProfileImageEntity image = businessProfileImageRepository.findFirstByBusinessProfile_UserIdAndCaption(user.getId(), "avatar");

        Map<String, Object> claims = new HashMap<>();


        claims.put("userId", userEntity.getId());
        List<String> roles = userEntity.getRoleEntities().stream()
                .map(RoleEntity::getName)
                .toList();
        claims.put("roles", roles);
        System.out.println(roles);
        logger.info("[ROLES] Đã thêm vào claims cho user {}: {}", userEntity.getUsername(), claims.get("roles"));

        String accessToken = generateAccessToken(username, claims);
        String refreshToken = generateRefreshToken(username);

        // lưu cache riêng biệt
        redisTemplate.opsForValue().set(accessKey, accessToken, Duration.ofMillis(accessTokenExpiration));
        redisTemplate.opsForValue().set(refreshKey, refreshToken, Duration.ofMillis(refreshTokenExpiration));

        return new TokenResponse(accessToken,
                refreshToken,
                accessTokenExpiration / 1000,
                refreshTokenExpiration / 1000,
                extractRoles(accessToken),
                username);
    }

    // Refresh access token bằng refresh token
    public TokenResponse refreshAccessToken(String refreshToken) {
        try {
            String username = getUsernameFromToken(refreshToken);

            if (!validateToken(refreshToken, username)) {
                throw new AuthException("Token sai hoặc ko tồn tại");
            }

            String refreshKey = redisKeyUtil.getRefreshTokenKey(username);
            String cachedRefresh = (String) redisTemplate.opsForValue().get(refreshKey);

            if (cachedRefresh == null || !cachedRefresh.equals(refreshToken)) {
                throw new AuthException("Không có refresh token trong cache");
            }

            logger.info("[REFRESH] Tạo access token mới cho user: {}", username);

            UserEntity userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));
//            BusinessProfileImageEntity image = businessProfileImageRepository.findFirstByBusinessProfile_UserIdAndCaption(user.getId(), "avatar");

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", userEntity.getRoleEntities().stream().map(RoleEntity::getName).toList());

            String newAccessToken = generateAccessToken(username, claims);

            String accessKey = redisKeyUtil.getAccessTokenKey(username);
            redisTemplate.opsForValue().set(accessKey, newAccessToken, Duration.ofMillis(accessTokenExpiration));

            return new TokenResponse(newAccessToken,
                    refreshToken,
                    accessTokenExpiration / 1000,
                    refreshTokenExpiration / 1000,
                    extractRoles(newAccessToken),
                    username);

        } catch (Exception e) {
            throw new BusinessException("Lỗi khi lấy refresh token: " + e.getMessage());
        }
    }

    public void logout(String token) {
        try {
            // bỏ "Bearer " nếu có
            String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            String username = getUsernameFromToken(rawToken);
            String accessKey = redisKeyUtil.getAccessTokenKey(username);
            String refreshKey = redisKeyUtil.getRefreshTokenKey(username);

            boolean delAccess = Boolean.TRUE.equals(redisTemplate.delete(accessKey));
            boolean delRefresh = Boolean.TRUE.equals(redisTemplate.delete(refreshKey));

            // lưu token vào blacklist (logout)
            String logoutKey = redisKeyUtil.getLogoutKey(rawToken);
            long ttl = getRemainingExpiration(rawToken);
            redisTemplate.opsForValue().set(logoutKey, "revoked", ttl, TimeUnit.MILLISECONDS);

            logger.info("[LOGOUT] User '{}' đã logout. AccessKey={}, RefreshKey={}, TokenRevoked={}",
                    username, delAccess, delRefresh, logoutKey);
        } catch (Exception e) {
            logger.error("[LOGOUT] Token không hợp lệ: {}", e.getMessage(), e);
            throw new AuthException("Sai token không thể đăng xuất.");
        }
    }

    /**
     * Vô hiệu hóa token rút tiền VÔ ĐIỀU KIỆN (Force Revoke).
     * Bất kể token còn hạn hay đã hết hạn, đều đưa vào Blacklist.
     */
    public void revokePayoutToken(String token) {
        String rawToken = extractToken(token);
        String logoutKey = redisKeyUtil.getPaymentOutKey(rawToken);
        long ttl;

        try {
            ttl = getRemainingExpiration(rawToken);
        } catch (Exception e) {

            ttl = -1;
        }

        if (ttl <= 0) {
            ttl = 30 * 60 * 1000L;
        }

        redisTemplate.opsForValue().set(logoutKey, "revoked", ttl, TimeUnit.MILLISECONDS);

        logger.info("[PAYOUT] Đã force revoke token. Key: {}, TTL set: {} ms", logoutKey, ttl);
    }


    /**
     * Lấy thời gian sống còn lại của JWT (để set TTL cho logoutKey).
     */
    private long getRemainingExpiration(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    private Set<String> getRoleNames(String username) {
        return userService.getRoleNames(username);
    }

    // =============== Helper Methods ===============

    public String getUsernameFromToken(String token) {
        return extractAllClaims(extractToken(token)).getSubject();
    }

    public String extractToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException("Token không được bỏ trống");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.trim();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(extractToken(token)).getExpiration();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(extractToken(token));
        return (java.util.List<String>) claims.get("roles");
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(extractToken(token))
                    .getBody();
        } catch (Exception e) {
            throw new AuthException(AuthMessage.FAILED_TOKEN.getMessage());
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(extractToken(token)).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public void forceLogout(String username) {
        try {
            String accessKey = redisKeyUtil.getAccessTokenKey(username);
            String refreshKey = redisKeyUtil.getRefreshTokenKey(username);

            redisTemplate.delete(Arrays.asList(accessKey, refreshKey));

            logger.info("[FORCE LOGOUT] Đã xóa token của user: {} để cập nhật Role mới", username);
        } catch (Exception e) {
            logger.error("[FORCE LOGOUT] Lỗi khi xóa token của user {}: {}", username, e.getMessage());
        }
    }
    public Boolean validateTokenStrict(String token) {
        try {
            String rawToken = extractToken(token);

            Claims claims = extractAllClaims(rawToken);
            String username = claims.getSubject();
            if (isTokenExpired(rawToken)) {
                return false;
            }
            String logoutKey = redisKeyUtil.getLogoutKey(rawToken);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(logoutKey))) {
                return false;
            }
            String accessKey = redisKeyUtil.getAccessTokenKey(username);
            String cachedAccess = (String) redisTemplate.opsForValue().get(accessKey);

            return rawToken.equals(cachedAccess);

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Hàm validate dành riêng cho Payout Token.
     * Nếu lỗi/hết hạn -> Ném BusinessException (HTTP 400)
     * Để Frontend chỉ hiện lỗi chứ KHÔNG Logout.
     */
    public void validatePayoutToken(String token) {
        String rawToken = extractToken(token);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(rawToken);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new BusinessException("Link xác nhận đã hết hạn. Vui lòng thực hiện lại lệnh rút tiền.");

        } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.SignatureException e) {
            throw new BusinessException("Link xác nhận không hợp lệ.");

        } catch (Exception e) {
            throw new BusinessException("Lỗi xác thực token: " + e.getMessage());
        }
        String payoutBlockKey = redisKeyUtil.getPaymentOutKey(rawToken);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(payoutBlockKey))) {
            throw new BusinessException("Link xác nhận này đã được sử dụng hoặc bị hủy.");
        }
    }
}
