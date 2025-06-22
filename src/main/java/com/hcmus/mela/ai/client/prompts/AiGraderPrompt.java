package com.hcmus.mela.ai.client.prompts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.ai-grader")
public class AiGraderPrompt {
    private String instruction;

    public String formatInstruction(Float correctScore) {
        return instruction.replace("{correctScore}", correctScore.toString());
    }
}
