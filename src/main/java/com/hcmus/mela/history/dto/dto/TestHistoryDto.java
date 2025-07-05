package com.hcmus.mela.history.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class TestHistoryDto {
    private UUID id;

    private UUID userId;

    private UUID levelId;

    private Double score;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private List<TestAnswerDto> answers;
}
