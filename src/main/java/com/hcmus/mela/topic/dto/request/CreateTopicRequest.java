package com.hcmus.mela.topic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTopicRequest {

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Image cannot be null")
    private String imageUrl;
}
