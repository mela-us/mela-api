package com.hcmus.mela.exercise.dto.response;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetExerciseInfoResponse {

    private String message;

    private ExerciseDto data;
}
