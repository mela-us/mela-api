package com.hcmus.mela.lecture.dto.response;

import com.hcmus.mela.lecture.dto.dto.ContributionDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetLectureContributionResponse {

    private String message;

    private ContributionDto data;
}
