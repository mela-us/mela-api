package com.hcmus.mela.topic.dto.response;

import com.hcmus.mela.topic.dto.dto.TopicDto;
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
