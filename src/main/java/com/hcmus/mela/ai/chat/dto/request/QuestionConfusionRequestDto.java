package com.hcmus.mela.ai.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionConfusionRequestDto {
    String text;
    
    String imageUrl;

    @NotNull(message = "Type must not be null")
    QuestionConfusionType type;
}
