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
public class TopicLevelHeatmapDataResponse {

    private String message;

    private List<LevelTopics> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelTopics {
        private String level;
        private List<Topic> topics;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Topic {
            private String name;
            private int value;
        }
    }
}