package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.HskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HskCategoryRepository extends JpaRepository<HskCategory, String> {
    boolean existsByName(String name);
}
