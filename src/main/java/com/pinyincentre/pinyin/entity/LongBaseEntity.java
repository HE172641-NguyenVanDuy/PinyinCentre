package com.pinyincentre.pinyin.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LongBaseEntity extends BaseEntity<Long> {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
