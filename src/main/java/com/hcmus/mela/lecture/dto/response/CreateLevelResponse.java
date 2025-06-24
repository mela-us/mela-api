package com.hcmus.mela.lecture.dto.response;

import com.hcmus.mela.lecture.dto.dto.LevelDto;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLevelResponse {

    private String message;

    private LevelDto data;
}
