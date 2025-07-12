package com.hcmus.mela.report.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AverageTimeStatDto {
    private double current;
    private double previous;
    private double percentChange;
}
