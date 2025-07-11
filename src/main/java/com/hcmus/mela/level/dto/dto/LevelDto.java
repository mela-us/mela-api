package com.hcmus.mela.level.dto.dto;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserDto;
import com.hcmus.mela.user.dto.dto.UserPreviewDto;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {

    private UUID levelId;

    private String name;

    private String imageUrl;

    private UUID createdBy;

    private UserPreviewDto creator;

    private String rejectedReason;

    private ContentStatus status;
}
