package com.hcmus.mela.history.dto.request;


import com.hcmus.mela.history.dto.dto.TestAnswerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Started time must not be null")
    @Schema(description = "Start time of the exercise", example = "2025-04-02T00:00:00")
    private LocalDateTime startedAt;

    @NotNull(message = "Completed time must not be null")
    @Schema(description = "End time of the exercise", example = "2025-04-02T00:01:00")
    private LocalDateTime completedAt;

    @NotNull(message = "List of answers must not be null")
    @NotEmpty(message = "List of answers must not be empty")
    @Schema(description = "List of answers")
    private List<TestAnswerDto> answers;
}
