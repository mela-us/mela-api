package com.hcmus.mela.level.dto.response;

import com.hcmus.mela.level.dto.dto.LevelDto;
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
