package com.hcmus.mela.ai.chatbot.dto.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hcmus.mela.shared.utils.LaTeXUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class AiResponseContent {
    private Map<String, Object> responseData;

    public static AiResponseContent fromJson(String jsonString) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        String dataString = LaTeXUtils.normalizeLaTeX(jsonString);
        Map<String, Object> data = gson.fromJson(dataString, type);

        return new AiResponseContent(data != null ? data : new HashMap<>());
    }

    public Map<String, Object> getIdentifyProblemResponse() {
        return getSafeResponse("analysis", "solution_method", "steps", "advice", "relative_terms");
    }

    public Map<String, Object> getResolveConfusionResponse() {
        return getSafeResponse("explain");
    }

    public Map<String, Object> getReviewSubmissionResponse() {
        return getSafeResponse("submission_summary", "status", "areas_for_improvement", "guidance", "encouragement");
    }

    public Map<String, Object> getSolution() {
        return getSafeResponse("final_answer", "steps", "advice", "problem_summary");
    }

    public Map<String, Object> getTitleConversation() {
        return getSafeResponse("title");
    }

    public Map<String, Object> getContextConversation() {
        return getSafeResponse("context");
    }

    public Map<String, Object> getCommonText() {
        return getSafeResponse("text");
    }

    private Map<String, Object> getSafeResponse(String... keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            if (responseData.containsKey(key) && responseData.get(key) != null) {
                result.put(key, responseData.get(key));
            }
        }
        return result.isEmpty() ? Collections.emptyMap() : result;
    }
}
