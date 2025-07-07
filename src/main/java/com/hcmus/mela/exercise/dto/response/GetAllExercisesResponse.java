package com.hcmus.mela.exercise.dto.response;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllExercisesResponse {

    private String message;

    private List<ExerciseDetailDto> data;
}
