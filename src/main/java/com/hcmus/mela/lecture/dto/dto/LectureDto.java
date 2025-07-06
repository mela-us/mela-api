package com.hcmus.mela.lecture.dto.dto;

import com.hcmus.mela.shared.type.ContentStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDto {

    private UUID lectureId;

    private UUID levelId;

    private UUID topicId;

    private String name;

    private Integer ordinalNumber;

    private String description;

    private List<SectionDto> sections;

    private ContentStatus status;

    private UUID createdBy;

    private String rejectReason;
}