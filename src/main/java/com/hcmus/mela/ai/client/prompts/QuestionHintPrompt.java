package com.hcmus.mela.ai.client.prompts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.exercise.hint")
public class QuestionHintPrompt {

    private Map<String, Map<String, String>> terms;

    private Map<String, Map<String, String>> guide;
}
