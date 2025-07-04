package com.hcmus.mela.history.dto.request;


import com.hcmus.mela.history.dto.dto.TestAnswerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResultRequest {
    @Schema(description = "Start time of the exercise", example = "2025-04-02T00:00:00")
    private LocalDateTime startedAt;

    @Schema(description = "End time of the exercise", example = "2025-04-02T00:01:00")
    private LocalDateTime completedAt;

    @Schema(description = "List of answers")
    private List<TestAnswerDto> answers;
}
