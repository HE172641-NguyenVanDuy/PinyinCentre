package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity<T> {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    protected T id;

    @Column(name = "created_date", nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    protected LocalDateTime updatedDate;

    @Column(name = "is_delete")
    protected Boolean isDelete;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        System.out.println("PrePersist called, setting createdDate & updatedDate: " + LocalDateTime.now());
        //updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
