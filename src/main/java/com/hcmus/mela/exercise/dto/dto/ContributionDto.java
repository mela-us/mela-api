package com.hcmus.mela.exercise.dto.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDto {

    private Integer verifiedNumber;

    private Integer totalCreatedNumber;

    private Integer totalQuestionVerifiedNumber;

    private Integer totalQuestionCreatedNumber;

    private Integer accessedContentNumber;
}