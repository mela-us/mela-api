package com.hcmus.mela.topic.dto.dto;

import com.hcmus.mela.shared.type.ContentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {

    private UUID topicId;

    private String name;

    private String imageUrl;

    private UUID createdBy;

    private String rejectedReason;

    private ContentStatus status;
}
