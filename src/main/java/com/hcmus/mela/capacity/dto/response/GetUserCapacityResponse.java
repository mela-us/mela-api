package com.hcmus.mela.capacity.dto.response;

import com.hcmus.mela.capacity.dto.UserCapacityDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserCapacityResponse {
    private String message;

    private List<UserCapacityDto> detailedStats;
}
