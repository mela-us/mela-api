package com.hcmus.mela.lecture.dto.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCount {

    private UUID topicId;

    private String name;

    private Integer totalCount;

    private Integer verifiedCount;
}