package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Role implements Serializable {

    @Id
    @Column(name = "role_name")
    String name;

    @Column(name = "description")
    String description;

    @ManyToMany
    @JoinTable(
            name = "role_permission", // tên bảng trung gian
            joinColumns = @JoinColumn(name = "role_name"), // khóa ngoại trỏ đến Role
            inverseJoinColumns = @JoinColumn(name = "permission_name") // khóa ngoại trỏ đến Permission
    )
    Set<Permission> permissions;

}
