package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "`library`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Library extends LongBaseEntity implements Serializable {

    @Column(name = "title", nullable = false)
    String title;

    @Column(name="file_link", nullable=false)
    String fileLink;

    @Column(name="course_type", nullable = false)
    String courseType;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    String status;
}
