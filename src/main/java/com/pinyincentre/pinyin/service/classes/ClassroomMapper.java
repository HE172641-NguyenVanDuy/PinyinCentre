package com.pinyincentre.pinyin.service.classes;


import com.pinyincentre.pinyin.dto.request.ClassRequest;
import com.pinyincentre.pinyin.entity.Classroom;
import com.pinyincentre.pinyin.service.utils.DateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ClassroomMapper {

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", qualifiedByName = "toLocalDateTime"),
            @Mapping(source = "endDate", target = "endDate", qualifiedByName = "toLocalDateTime")
    })
    Classroom toClassroom(ClassRequest request);

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", qualifiedByName = "toLocalDateTime"),
            @Mapping(source = "endDate", target = "endDate", qualifiedByName = "toLocalDateTime")
    })
    void updateClassroomFromRequest(ClassRequest request, @MappingTarget Classroom classroom);

    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
