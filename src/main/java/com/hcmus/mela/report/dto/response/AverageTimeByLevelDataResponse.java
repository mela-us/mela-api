package com.hcmus.mela.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AverageTimeByLevelDataResponse {

    private String message;

    private List<LevelTime> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelTime {
        private String level;
        private int averageMinutes;
    }
}