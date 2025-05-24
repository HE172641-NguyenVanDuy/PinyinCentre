package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class UUIDBaseEntity extends BaseEntity<String> {
//@GeneratedValue(strategy = GenerationType.UUID)

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    protected String id;
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
