package com.hcmus.mela.level.dto.response;

import com.hcmus.mela.level.dto.dto.LevelDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLevelsResponse {

    private String message;

    private Integer total;

    private List<LevelDto> data;
}
