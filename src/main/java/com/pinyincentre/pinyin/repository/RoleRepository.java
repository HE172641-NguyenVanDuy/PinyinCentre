package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}