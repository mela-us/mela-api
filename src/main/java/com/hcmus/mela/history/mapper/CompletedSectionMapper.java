package com.hcmus.mela.history.mapper;

import com.hcmus.mela.history.dto.dto.CompletedSectionDto;
import com.hcmus.mela.history.model.LectureCompletedSection;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompletedSectionMapper {

    CompletedSectionMapper INSTANCE = Mappers.getMapper(CompletedSectionMapper.class);

    CompletedSectionDto lectureCompletedSectionToCompletedSectionDto(LectureCompletedSection section);

    @Named("lectureCompletedSectionsToCompletedSectionDtos")
    List<CompletedSectionDto> lectureCompletedSectionsToCompletedSectionDtos(List<LectureCompletedSection> sections);
}
