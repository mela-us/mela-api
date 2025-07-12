package com.hcmus.mela.lecture.dto.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDto {

    private Integer verifiedNumber;

    private Integer totalCreatedNumber;

    private Integer accessedContentNumber;

    private List<TopicCount> totalCreatedNumberCountByTopic;
}