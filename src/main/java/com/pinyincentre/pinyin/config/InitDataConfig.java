package com.pinyincentre.pinyin.config;

import com.pinyincentre.pinyin.entity.Permission;
import com.pinyincentre.pinyin.entity.RoleEntity;
import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.repository.PermissionRepository;
import com.pinyincentre.pinyin.repository.RoleRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDataConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing DB Data if not exists...");
        initPermissions();
        initRolesAndPermissions();
        initDefaultUsers();
        log.info("DB Initialization Completed.");
    }

    private void initPermissions() {
        if (permissionRepository.count() == 0) {
            String[] permissions = {
                    "ADD_DOCUMENTS", "ASSIGN_STUDENT", "BAN_USER", "CREATE_ACCOUNT_STUDENT",
                    "CREATE_CLASS", "CREATE_COURSE", "CREATE_DOCUMENTS", "DELETE_CLASS",
                    "DELETE_COURSE", "REMOVE_DOCUMENTS", "UPDATE_CLASS", "UPDATE_COURSE",
                    "UPDATE_DOCUMENTS", "VIEW_CLASS_LIST", "VIEW_COURSE", "VIEW_DOCUMENTS",
                    "VIEW_LIST_USER_ACTIVE"
            };

            for (String p : permissions) {
                permissionRepository.save(Permission.builder().name(p).description(p).build());
            }
        }
    }

    private void initRolesAndPermissions() {
        if (roleRepository.count() == 0) {
            Map<String, String[]> rolePermMap = Map.of(
                    "ADMIN", new String[]{"BAN_USER", "CREATE_ACCOUNT_STUDENT", "VIEW_LIST_USER_ACTIVE"},
                    "CENTRE_OWNER", new String[]{
                            "CREATE_ACCOUNT_STUDENT", "CREATE_CLASS", "CREATE_COURSE", "CREATE_DOCUMENTS",
                            "DELETE_CLASS", "DELETE_COURSE", "REMOVE_DOCUMENTS", "UPDATE_CLASS",
                            "UPDATE_COURSE", "UPDATE_DOCUMENTS", "VIEW_CLASS_LIST", "VIEW_COURSE", "VIEW_DOCUMENTS"
                    },
                    "STUDENT", new String[]{"VIEW_CLASS_LIST", "VIEW_COURSE", "VIEW_DOCUMENTS"},
                    "TEACHER", new String[]{"CREATE_DOCUMENTS", "REMOVE_DOCUMENTS", "UPDATE_DOCUMENTS", "VIEW_CLASS_LIST", "VIEW_COURSE", "VIEW_DOCUMENTS"}
            );

            rolePermMap.forEach((roleName, perms) -> {
                Set<Permission> permissionSet = new HashSet<>();
                for (String permName : perms) {
                    permissionRepository.findById(permName).ifPresent(permissionSet::add);
                }
                RoleEntity role = RoleEntity.builder()
                        .name(roleName)
                        .description(roleName + " role")
                        .permissions(permissionSet)
                        .build();
                roleRepository.save(role);
            });
        }
    }

    private void initDefaultUsers() {
        String defaultPassword = passwordEncoder.encode("123456");

        String[][] users = {
                {"admin", "admin@test.com", "ADMIN"},
                {"owner", "owner@test.com", "CENTRE_OWNER"},
                {"student", "student@test.com", "STUDENT"},
                {"teacher", "teacher@test.com", "TEACHER"}
        };

        for (String[] u : users) {
            if (!userRepository.existsByUsername(u[0])) {
                Set<RoleEntity> roles = new HashSet<>();
                roleRepository.findById(u[2]).ifPresent(roles::add);

                UserEntity user = UserEntity.builder()
                        .username(u[0])
                        .email(u[1])
                        .password(defaultPassword)
                        .fullName(u[0] + " User")
                        .status(1)
                        .enabled(true)
                        .roleEntities(roles)
                        .build();
                userRepository.save(user);
            }
        }
    }
}
