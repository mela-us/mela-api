package com.hcmus.mela.lecture.dto.response;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllLecturesResponse {

    private String message;

    private List<LectureDto> data;
}
