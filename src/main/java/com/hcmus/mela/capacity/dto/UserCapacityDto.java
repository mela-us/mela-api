package com.hcmus.mela.capacity.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCapacityDto {
    private String topicName;
    private Double excellence;
}
