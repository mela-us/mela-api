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
public class UserGrowthDataResponse {

    private String message;

    private List<UserGrowth> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGrowth {
        private String month;
        private int users;
    }
}
