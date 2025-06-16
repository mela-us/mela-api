package com.hcmus.mela.ai.client.prompts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.lecture-confusion")
public class LectureConfusionPrompt {
    private String instruction;
}
