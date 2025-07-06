package com.hcmus.mela.lecture.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLectureRequest {

    private UUID topicId;

    private UUID levelId;

    private String name;

    private Integer ordinalNumber;

    private String description;

    private List<UpdateSectionRequest> sections;
}
