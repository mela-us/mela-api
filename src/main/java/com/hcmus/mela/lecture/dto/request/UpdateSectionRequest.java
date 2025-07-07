package com.hcmus.mela.lecture.dto.request;

import com.hcmus.mela.lecture.model.SectionType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSectionRequest {

    private Integer ordinalNumber;

    private String name;

    private String content;

    private String url;

    private SectionType sectionType;
}
