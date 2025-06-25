package com.hcmus.mela.lecture.dto.request;

import com.hcmus.mela.lecture.model.SectionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSectionRequest {

    @NotNull(message = "Ordinal number cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "Name cannot be null")
    private String name;

    private String content;

    private String url;

    @NotNull(message = "Section type cannot be null")
    private SectionType sectionType;
}
