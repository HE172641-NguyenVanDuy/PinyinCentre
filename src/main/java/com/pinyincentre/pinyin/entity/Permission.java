package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Permission {

    @Id
    @Column(name = "permission_name")
    String name;

    @Column(name = "description")
    String description;
}
