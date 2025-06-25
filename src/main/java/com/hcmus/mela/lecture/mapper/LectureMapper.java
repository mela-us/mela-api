package com.hcmus.mela.lecture.mapper;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.LectureOfSectionDto;
import com.hcmus.mela.lecture.dto.dto.LectureStatDetailDto;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.model.LectureActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = LectureSectionMapper.class)
public interface LectureMapper {

    LectureMapper INSTANCE = Mappers.getMapper(LectureMapper.class);

    @Mapping(source = "sections", target = "sections", qualifiedByName = "createSectionRequestsToSections")
    Lecture createLectureRequestToLecture(CreateLectureRequest createLectureRequest);

    @Mapping(source = "sections", target = "sections", qualifiedByName = "sectionsToSectionDtos")
    LectureDto lectureToLectureDto(Lecture lecture);

    LectureOfSectionDto lectureToLectureOfSectionDto(Lecture lecture);

    LectureStatDetailDto lectureToLectureStatDetailDto(Lecture lecture);

    Lecture lectureActivityToLecture(LectureActivity lectureActivity);
}