package com.hcmus.mela.lecture.mapper;

import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.request.CreateSectionRequest;
import com.hcmus.mela.lecture.dto.request.UpdateSectionRequest;
import com.hcmus.mela.lecture.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LectureSectionMapper {

    LectureSectionMapper INSTANCE = Mappers.getMapper(LectureSectionMapper.class);

    Section sectionDtoToSection(SectionDto sectionDto);

    @Named("sectionDtosToSections")
    List<Section> sectionDtosToSections(List<SectionDto> sectionDtos);

    SectionDto sectionToSectionDto(Section section);

    @Named("sectionsToSectionDtos")
    List<SectionDto> sectionsToSectionDtos(List<Section> sections);

    Section createSectionRequestToSection(CreateSectionRequest createSectionRequest);

    @Named("createSectionRequestsToSections")
    List<Section> createSectionRequestsToSections(List<CreateSectionRequest> createSectionRequests);

    Section updateSectionRequestToSection(UpdateSectionRequest updateSectionRequest);

    @Named("updateSectionRequestsToSections")
    List<Section> updateSectionRequestsToSections(List<UpdateSectionRequest> updateSectionRequests);
}
