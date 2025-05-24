package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;

import java.util.UUID;

@MappedSuperclass
public abstract class UUIDBaseEntity extends BaseEntity<String> {

//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id", updatable = false, nullable = false)
//    protected String id;
//
//
//    @Override
//    protected void onCreate() {
//        super.onCreate();
//        if (id == null) {
//            id = UUID.randomUUID().toString();
//        }
//    }

    @Override
    protected void onCreate() {
        if (getId() == null) {
            setId(UUID.randomUUID().toString()); // dùng setter, không tạo biến mới
        }
        super.onCreate();
    }
}
