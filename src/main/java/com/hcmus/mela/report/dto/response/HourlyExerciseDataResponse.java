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
public class HourlyExerciseDataResponse {

    private String message;

    private List<HourlyActivity> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyActivity {
        private String hour;
        private int count;
    }
}