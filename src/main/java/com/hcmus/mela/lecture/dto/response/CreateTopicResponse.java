package com.hcmus.mela.lecture.dto.response;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTopicResponse {

    private String message;

    private TopicDto data;
}
