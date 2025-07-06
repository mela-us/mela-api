package com.hcmus.mela.history.dto.request;

import com.hcmus.mela.history.dto.dto.ExerciseAnswerDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResultRequest {

    @NotNull(message = "Exercise id cannot be null")
    private UUID exerciseId;

    @NotNull(message = "Started time cannot be null")
    private LocalDateTime startedAt;

    @NotNull(message = "Completed time cannot be null")
    private LocalDateTime completedAt;

    @NotNull(message = "Answers cannot be null")
    @NotEmpty(message = "Answers cannot be empty")
    private List<@Valid ExerciseAnswerDto> answers;
}
