package com.pinyincentre.pinyin.annotation;

import com.pinyincentre.pinyin.constant.RoleType;
import com.pinyincentre.pinyin.dto.ResultInfo;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.service.user.UserService;
import com.pinyincentre.pinyin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionAspect {

    private Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Around("execution(* *(..)) && @annotation(permission)")
    public Object invoke(final ProceedingJoinPoint pjp, Permission permission) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(permission.target());
        if (token == null) {
            return ResultInfo.builder()
                    .status(ResultInfo.RESULT_NOK_403)
                    .message("Unauthorized")
                    .build();
        }

        token = token.replace("Bearer ", "");
        String userName = jwtUtil.getUsernameFromToken(token);

        boolean rlt = hasPermission(userName, permission.roles());
        if (rlt) {
            return pjp.proceed();
        } else {
            return ResultInfo.builder()
                    .status(ResultInfo.RESULT_NOK_403)
                    .message(permission.message())
                    .build();
        }
    }

    public boolean hasPermission(String userName, RoleType[] requiredRoles) {
        UserEntity userEntity = userService.getUserByUserName(userName);
        if (userEntity == null) return false;

        return userEntity.getRoleEntities().stream()
                .map(RoleEntity::getName)  // giả sử Role.getName() = "ROLE_ADMIN"
                .anyMatch(userRole ->
                        Arrays.stream(requiredRoles)
                                .anyMatch(reqRole -> userRole.equals(reqRole.name()))
                );
    }

}

