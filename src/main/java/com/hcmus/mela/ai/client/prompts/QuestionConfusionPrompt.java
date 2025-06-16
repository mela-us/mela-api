package com.hcmus.mela.ai.client.prompts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.question-confusion")
public class QuestionConfusionPrompt {
    private ClarifyQuestion clarifyQuestion;
    private ExplainSolution explainSolution;
    private CustomText customText;

    @Getter
    @Setter
    public static class ClarifyQuestion {
        private String instruction;
    }

    @Getter
    @Setter
    public static class ExplainSolution {
        private String instruction;
    }

    @Getter
    @Setter
    public static class CustomText {
        private String instruction;
    }
}
