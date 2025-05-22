package com.pinyincentre.pinyin.service.library;

import com.pinyincentre.pinyin.dto.request.LibraryRequest;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;

import java.util.List;

public interface LibraryService {
    LibraryResponse createFileLinkInLibrary(LibraryRequest libraryRequest);
    LibraryResponse updateFileLinkInLibrary(LibraryRequest libraryRequest, Long id);
    LibraryResponse deleteFileInLibrary(Long id);
    LibraryResponse getFileInLibraryById(Long id);
    List<LibraryResponse> getAllFilesInLibrary(Integer pageSize, int currentPage);
}
