package com.hcmus.mela.ai.chat.dto.request;

import com.hcmus.mela.shared.validator.AtLeastOneNotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AtLeastOneNotEmpty(fields = {"text", "imageUrl"}, message = "At least one of text or image url must be provided")
public class LectureConfusionRequestDto {

    String text;

    String imageUrl;

    @NotEmpty(message = "File url must not be empty")
    String fileUrl;

    @NotNull(message = "Current page must not be empty")
    Integer currentPage;
}
