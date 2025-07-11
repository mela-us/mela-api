package com.hcmus.mela.review.dto.request;

import com.hcmus.mela.review.model.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {

    private ReviewType type;

    private UUID itemId;

    private Integer ordinalNumber;

    private Boolean isDone;
}
