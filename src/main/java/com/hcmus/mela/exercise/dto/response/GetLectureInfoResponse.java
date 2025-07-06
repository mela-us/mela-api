package com.hcmus.mela.exercise.dto.response;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLectureInfoResponse {

    private String message;

    private LectureDto data;
}
