package com.hcmus.mela.lecture.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateLectureRequest {

    @NotNull(message = "Topic ID cannot be null")
    private UUID topicId;

    @NotNull(message = "Level ID cannot be null")
    private UUID levelId;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Ordinal number cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Sections cannot be null")
    @NotEmpty(message = "Sections list cannot be empty")
    private List<@Valid CreateSectionRequest> sections;
}
