package com.hcmus.mela.exercise.dto.response;

import com.hcmus.mela.exercise.dto.dto.ContributionDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetExerciseContributionResponse {

    private String message;

    private ContributionDto data;
}
