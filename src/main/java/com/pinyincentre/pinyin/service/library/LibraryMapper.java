package com.pinyincentre.pinyin.service.library;

import com.pinyincentre.pinyin.dto.request.LibraryRequest;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.entity.Library;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    Library toLibrary(LibraryRequest request);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    LibraryResponse toLibraryResponse(Library library);

    void updateLibraryFromRequest(LibraryRequest request, @MappingTarget Library library);

}
