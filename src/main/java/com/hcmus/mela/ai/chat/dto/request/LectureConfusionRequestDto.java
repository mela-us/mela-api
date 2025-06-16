package com.hcmus.mela.ai.chat.dto.request;

import com.hcmus.mela.shared.validator.AtLeastOneNotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AtLeastOneNotEmpty(fields = {"text", "imageUrl"}, message = "Field must not be empty")
public class LectureConfusionRequestDto {
    String text;
    String imageUrl;

    @NotEmpty(message = "Field fileUrl must not be empty")
    String fileUrl;

    @NotNull(message = "Field fileUrl must not be empty")
    Integer currentPage;
}
