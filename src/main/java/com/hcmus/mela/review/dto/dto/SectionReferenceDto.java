package com.hcmus.mela.review.dto.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionReferenceDto {

    private UUID lectureId;

    private Integer ordinalNumber;

    private Boolean isDone;

    private String lectureTitle;

    private String topicTitle;

    private String levelTitle;

    private String sectionUrl;
}
