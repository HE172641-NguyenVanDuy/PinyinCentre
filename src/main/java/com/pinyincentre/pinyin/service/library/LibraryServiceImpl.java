package com.pinyincentre.pinyin.service.library;

import com.pinyincentre.pinyin.dto.request.LibraryRequest;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.entity.Library;
import com.pinyincentre.pinyin.exception.AppException;
import com.pinyincentre.pinyin.exception.ErrorCode;
import com.pinyincentre.pinyin.repository.LibraryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "LIBRARY-SERVICE")
public class LibraryServiceImpl implements LibraryService{

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private LibraryMapper libraryMapper;

    @Override
    public LibraryResponse createFileLinkInLibrary(LibraryRequest libraryRequest) {
        log.info("Create file link in library");
        log.info("LibraryRequest: {}", libraryRequest);
        Library library = libraryMapper.toLibrary(libraryRequest);
        log.info("library: {}", library);
        if(library != null) {
            library.setStatus(LibraryStatus.ACTIVE.getStatusName());
            log.info("library after save to db: {}", library);
            return libraryMapper.toLibraryResponse(libraryRepository.save(library));
        }
        return null;
    }

    @Transactional
    @Override
    public LibraryResponse updateFileLinkInLibrary(LibraryRequest libraryRequest, Long id) {
        log.info("Update file link in library");
        Library library = libraryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        log.info("library: {}", library);
        libraryMapper.updateLibraryFromRequest(libraryRequest, library);
        Library savedLibrary = libraryRepository.save(library);
        log.info("savedLibrary: {}", savedLibrary);
        return libraryMapper.toLibraryResponse(savedLibrary);
    }

    @Override
    public LibraryResponse deleteFileInLibrary(Long id) {
        Library library = libraryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        library.setStatus(LibraryStatus.INACTIVE.getStatusName());
        log.info("library: {}", library);
        Library updatedLibrary = libraryRepository.save(library);
        log.info("updatedLibrary: {}", updatedLibrary);
        return libraryMapper.toLibraryResponse(updatedLibrary);
    }

    @Override
    public LibraryResponse getFileInLibraryById(Long id) {
        Library library = libraryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
        log.info("library: {}", library);
        return libraryMapper.toLibraryResponse(library);
    }

    @Override
    public List<LibraryResponse> getAllFilesInLibrary(Integer pageSize, int currentPage) {

        log.info("Current page: {}, page size: {}", currentPage, pageSize);

        int pageIndex = currentPage - 1; // Trang bắt đầu từ 0
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<LibraryResponse> page = libraryRepository.findLibraryByStatus(LibraryStatus.ACTIVE.getStatusName(), pageable);
        return page.getContent();
    }
}
