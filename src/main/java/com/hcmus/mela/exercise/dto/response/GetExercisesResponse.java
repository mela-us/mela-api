package com.hcmus.mela.exercise.dto.response;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetExercisesResponse {

    private String message;

    private List<ExerciseDetailDto> data;
}
