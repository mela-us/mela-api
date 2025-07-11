package com.hcmus.mela.exercise.dto.dto;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserPreviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDetailDto {

    private UUID exerciseId;

    private UUID lectureId;

    private String exerciseName;

    private Integer ordinalNumber;

    private Integer totalQuestions;

    private ContentStatus status;

    private UUID createdBy;

    private UserPreviewDto creator;

    private String rejectedReason;
}