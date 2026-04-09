package com.pinyincentre.pinyin.service.authentication;

import com.auth0.jwt.JWT;
import com.pinyincentre.pinyin.constant.AuthMessage;
import com.pinyincentre.pinyin.constant.RoleType;
import com.pinyincentre.pinyin.dto.*;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.exception.BusinessException;
import com.pinyincentre.pinyin.repository.RoleRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.util.JwtUtil;
import com.pinyincentre.pinyin.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Data
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    //private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final GoogleOAuthProperties googleOAuthProperties;
//    private final CartRepository cartRepository;
    @Value("${spring.mail.urlResetPassword}")
    private String urlResetPasswordCallBack;
    @Value("${spring.mail.activeAccountURL}")
    private String activeAccountCallBackUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public String register(String username, String email, String password) {
        Optional<UserEntity> checkByEmail = userRepository.findByEmail(email);
        if (checkByEmail.isPresent() && checkByEmail.get().getEnabled()) {
            throw new BusinessException(AuthMessage.EMAIL_EXISTS.getMessage());
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(AuthMessage.USERNAME_EXISTS.getMessage());
        }
        UserEntity user = new UserEntity();
        if (checkByEmail.isPresent()) {
            user = checkByEmail.get();
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Set default roles here if needed
        RoleEntity role = roleRepository.getByName("ROLE_USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        user.setRoleEntities(roles);
//        user.setAccountBalance(0.0);
        user.setEnabled(false);
        userRepository.save(user);

        String activeAccountToken = jwtUtil.generateActiveAccount(user.getEmail(), 15L);
        String key = redisKeyUtil.getActiveAccountKey(user.getEmail());
        redisTemplate.opsForValue().set(key, activeAccountToken, 15, TimeUnit.MINUTES);
        String resetLink = activeAccountCallBackUrl + activeAccountToken;

//        emailService.sendEmailWithTemplate(
//                user.getEmail(),
//                EmailEnum.SUBJECT_ACTIVE_ACCOUNT.getMessage(),
//                resetLink,
//                EmailEnum.TEMPLATE_ACTIVE_ACCOUNT.getMessage()
//        );
        return "Vui lòng kiểm tra email dể kích hoạt tài khoản";
    }

    // Sửa trong AuthServiceImpl.java
    public TokenResponse login(String usernameOrEmail, String password) {
        UserEntity user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BusinessException(AuthMessage.USER_NOT_FOUND.getMessage()));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException("Tài khoản đã bị khóa hoặc chưa được kích hoạt. Vui lòng liên hệ hỗ trợ.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(AuthMessage.INVALID_PASSWORD.getMessage());
        }


        return jwtUtil.generateTokens(user);
    }

    public String generateGoogleAuthUrl(HttpServletRequest request) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        if (clientRegistration == null) {
            log.error("Không tìm thấy client registration 'google'");
            throw new RuntimeException(AuthMessage.LOGIN_FAILED_WITH_GOOGLE.getMessage());
        }

//        String redirectUri = clientRegistration.getRedirectUri();
        log.info("Using redirect_uri: {}", googleOAuthProperties.getRedirectUri());
        String state = UUID.randomUUID().toString();

        try {
            redisTemplate.opsForValue().set("oauth_state:" + state, true, Duration.ofMinutes(10));
            log.info("Đã lưu state vào Redis: oauth_state:{}", state);
        } catch (Exception e) {
            log.error("Lỗi khi lưu state vào Redis: {}", e.getMessage());
            throw new RuntimeException(AuthMessage.LOGIN_FAILED_WITH_GOOGLE.getMessage());
        }

        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(googleOAuthProperties.getRedirectUri())
                .scopes(clientRegistration.getScopes())
                .state(state)
                .build();

        String authUri = clientRegistration.getProviderDetails().getAuthorizationUri() +
                "?client_id=" + clientRegistration.getClientId() +
                "&redirect_uri=" + URLEncoder.encode(googleOAuthProperties.getRedirectUri(), StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(String.join(" ", clientRegistration.getScopes()), StandardCharsets.UTF_8) +
                "&state=" + URLEncoder.encode(authRequest.getState(), StandardCharsets.UTF_8);

        log.info("Generated Google auth URL: {}", authUri);
        return authUri;
    }

    @Override
    public TokenResponse authenticateAndFetchProfile(String code, String state) {

        String stateKey = "oauth_state:" + state;
        if (state == null || !Boolean.TRUE.equals(redisTemplate.hasKey(stateKey))) {
            log.error("Invalid or expired state parameter: {}", state);
            throw new BusinessException(AuthMessage.STATE_EXPIRED.getMessage());

        }

        // Kiểm tra mã code đã sử dụng chưa
        String codeKey = "google_code:" + code;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(codeKey))) {
            log.error("Authorization code already used: {}", code);
            throw new BusinessException(AuthMessage.INVALID_CODE.getMessage());
        }
        //Dùng code để gọi Google Token API => lấy access_token + id_token
        GoogleTokenResponse tokenResponse = exchangeCodeForToken(code, state);
        log.info("Google auth token: {}", tokenResponse);
        //Dùng access_token hoặc id_token để lấy thông tin profile
        GoogleUserInfo userInfo = getUserInfo(tokenResponse);
        log.info("User info: {}", userInfo);
        UserEntity user = findOrCreateFromGoogle(userInfo);
        log.info(user.toString());

        // Lưu mã code đã sử dụng vào Redis với TTL 5 phút
        try {
            redisTemplate.opsForValue().set(codeKey, true, Duration.ofMinutes(5));
            log.info("Stored used code in Redis: {}", codeKey);
        } catch (Exception e) {
            log.error("Failed to store code in Redis: {}", e.getMessage());
        }

        // Xóa state khỏi Redis
        try {
            redisTemplate.delete(stateKey);
            log.info("Deleted state from Redis: {}", stateKey);
        } catch (Exception e) {
            log.error("Failed to delete state from Redis: {}", e.getMessage());
        }

        return jwtUtil.generateTokens(user);
    }


    public GoogleTokenResponse exchangeCodeForToken(String code, String state) {

        String tokenUrl = googleOAuthProperties.getTokenUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.info("Sending request to Google token endpoint with params: {}", params);
            ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    GoogleTokenResponse.class
            );

            log.info("Google token response: {}", response.getBody());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST &&
                    e.getResponseBodyAsString().contains("invalid_grant")) {
                throw new BusinessException(AuthMessage.INVALID_GRANT.getMessage());
            }
            throw new BusinessException("Failed to exchange code: " + e.getResponseBodyAsString());
        }

    }

    public GoogleUserInfo getUserInfo(GoogleTokenResponse tokenResponse) {
        String idToken = tokenResponse.getId_token();

        // Decode JWT bằng thư viện java-jwt (com.auth0:java-jwt)
        DecodedJWT decoded = JWT.decode(idToken);

        GoogleUserInfo userInfo = new GoogleUserInfo();
        userInfo.setEmail(decoded.getClaim("email").asString());
        userInfo.setName(decoded.getClaim("name").asString());


        return userInfo;
    }

    public UserEntity findOrCreateFromGoogle(GoogleUserInfo userInfo) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(userInfo.getEmail());

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(userInfo.getEmail().split("@")[0]);
        newUser.setEmail(userInfo.getEmail());
        newUser.setFullName(userInfo.getName());
        // set role if needed
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleRepository.getByName(RoleType.ROLE_USER.name()));
        newUser.setRoleEntities(roles);
//        newUser.setAccountBalance(0.0);
        return userRepository.save(newUser);
    }

    public TokenResponse getNewAccessToken(String refreshToken) {
        return jwtUtil.refreshAccessToken(refreshToken);
    }

    @Override
    public void logout(String token) {
        jwtUtil.logout(token);
    }

    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByUsername(email)
                .or(() -> userRepository.findByEmail(email))
                .orElseThrow(() -> new BusinessException(AuthMessage.USER_NOT_FOUND.getMessage()));

        String resetToken = jwtUtil.generateResetToken(user.getEmail(), 15L);
        String key = redisKeyUtil.getResetPasswordToken(user.getEmail());
        redisTemplate.opsForValue().set(key, resetToken, 15, TimeUnit.MINUTES);
        System.out.println(resetToken);
        String resetLink = urlResetPasswordCallBack + resetToken;

//        emailService.sendEmailWithTemplate(
//                user.getEmail(),
//                EmailEnum.SUBJECT_CHANGE_PASSWORD.getMessage(),
//                resetLink,
//                EmailEnum.TEMPLATE_CHANGE_PASSWORD.getMessage()
//        );

    }

    @Override
    public void resetPassword(String newPassword, String token) {
        try {
            String email = jwtUtil.getUsernameFromToken(token);
            if (email == null) {
                throw new BusinessException(AuthMessage.TOKEN_INVALID.getMessage());
            }
            String key = redisKeyUtil.getResetPasswordToken(email);
            String tokenInRedis = (String) redisTemplate.opsForValue().get(key);

            if (tokenInRedis == null) {
                throw new BusinessException(AuthMessage.TOKEN_NOT_EXIST.getMessage());
            }

            if (!token.equals(tokenInRedis)) {
                throw new BusinessException(AuthMessage.TOKEN_INVALID.getMessage());
            }

            UserEntity user = userRepository.getFirstByEmail(email);
            if (user == null) {
                throw new BusinessException(AuthMessage.USER_NOT_FOUND.getMessage());
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new BusinessException(AuthMessage.TOKEN_INVALID.getMessage());
        }
    }

    private String normalizeRole(String roleName) {
        return roleName != null && roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
    }

    private String principalToLookupKey(Object principal) {
        if (principal == null) return null;
        if (principal instanceof UserDetails ud) return ud.getUsername();
        if (principal instanceof String s) return s;            // có thể là username hoặc email
        if (principal instanceof UserEntity ue) return ue.getUsername();
        return null;
    }

    @Override
    public UserEntity getCurrentUserOrNull() {
        var ctx = SecurityContextHolder.getContext();
        if (ctx == null) return null;
        Authentication auth = ctx.getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        String key = principalToLookupKey(auth.getPrincipal());
        if (key == null || "anonymousUser".equals(key)) return null;

        // 1 query: username OR email (case-insensitive)
        return userRepository.findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(key, key).orElse(null);
    }

    @Override
    public UserEntity requireUser() {
        var u = getCurrentUserOrNull();
        if (u == null) throw new BusinessException(AuthMessage.USER_NOT_FOUND.getMessage());
        return u;
    }

    @Override
    public boolean hasRole(String roleName) {
        var ctx = SecurityContextHolder.getContext();
        if (ctx == null) return false;
        var auth = ctx.getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        String expected = normalizeRole(roleName);
        return auth.getAuthorities().stream().anyMatch(a -> expected.equals(a.getAuthority()));
    }

    @Override
    public String activeAccount(String token) {
        String email = jwtUtil.getUsernameFromToken(token);
        if (email == null) throw new BusinessException(AuthMessage.TOKEN_INVALID.getMessage());
        UserEntity user = userRepository.getFirstByEmail(email);
        if (user == null) throw new BusinessException(AuthMessage.USER_NOT_FOUND.getMessage());
        user.setEnabled(true);
//        userRepository.save(user);
//        CartEntity cartEntity = CartEntity.builder()
//                .customer(userRepository.save(user))
//                .build();
//        cartRepository.save(cartEntity);
        return "Tài khoản đã được kích hoạt, đăng nhập để tiếp tục";
    }

    @Override
    public void changePassword(ChangePasswordDTO payload, String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userRepository.getFirstByUsername(username);
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException("Tài khoản đã bị khóa hoặc chưa được kích hoạt. Vui lòng liên hệ hỗ trợ.");
        }

        if (!passwordEncoder.matches(payload.getOldPassword(), user.getPassword())) {
            throw new BusinessException(AuthMessage.INVALID_PASSWORD_CHANGE_PASSWORD.getMessage());
        }
        if (payload.getOldPassword().equals(payload.getNewPassword())) {
            throw new BusinessException(AuthMessage.EQUAL_PASSWORD_CHANGE.getMessage());
        }
        user.setPassword(passwordEncoder.encode(payload.getNewPassword()));
        userRepository.save(user);
    }


    public String registerByAdmin(String username, String email, String password) {
        Optional<UserEntity> checkByEmail = userRepository.findByEmail(email);
        if (checkByEmail.isPresent() && checkByEmail.get().getEnabled()) {
            throw new BusinessException(AuthMessage.EMAIL_EXISTS.getMessage());
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(AuthMessage.USERNAME_EXISTS.getMessage());
        }
        UserEntity user = new UserEntity();
        if (checkByEmail.isPresent()) {
            user = checkByEmail.get();
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Set default roles here if needed
        RoleEntity role = roleRepository.getByName("ROLE_ADMIN_SYSTEM");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        user.setRoleEntities(roles);
        user.setEnabled(true);
        userRepository.save(user);
        return "Tạo tài khoản thành công ";
    }

}

