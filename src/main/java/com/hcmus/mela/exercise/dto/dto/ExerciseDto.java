package com.hcmus.mela.exercise.dto.dto;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserPreviewDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private UUID exerciseId;

    private UUID lectureId;

    private String exerciseName;

    private Integer ordinalNumber;

    private List<QuestionDto> questions;

    private ContentStatus status;

    private UUID createdBy;

    private UserPreviewDto creator;

    private String rejectedReason;
}