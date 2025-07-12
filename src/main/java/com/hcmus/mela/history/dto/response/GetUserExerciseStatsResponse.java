package com.hcmus.mela.history.dto.response;

import com.hcmus.mela.history.dto.dto.UserExerciseStatsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserExerciseStatsResponse {

    private String message;

    private UserExerciseStatsDto data;
}
