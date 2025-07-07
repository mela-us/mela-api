package com.hcmus.mela.history.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveSectionRequest {

    @NotNull(message = "Lecture id cannot be null")
    private UUID lectureId;

    @NotNull(message = "Ordinal number of section cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "Complete time cannot be null")
    private LocalDateTime completedAt;
}
