package com.hcmus.mela.report.dto.response;

import com.hcmus.mela.report.dto.dto.StatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedExercisesStatResponse {

    private String message;

    private StatDto data;
}