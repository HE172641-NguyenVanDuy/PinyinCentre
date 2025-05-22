package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.entity.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Long> {

    @Query("SELECT " +
            "new com.pinyincentre.pinyin.dto.response.LibraryResponse(" +
            "l.id, l.title, l.fileLink, l.courseType, l.description, l.createdDate) " +
            "" +
            "FROM Library l WHERE l.status = :status ORDER BY l.createdDate DESC")
    Page<LibraryResponse> findLibraryByStatus(@Param("status") String status, Pageable pageable);
}
