package com.hcmus.mela.ai.chat.dto.request;

import com.hcmus.mela.shared.validator.AtLeastOneNotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AtLeastOneNotEmpty(fields = {"text", "imageUrl"}, message = "At least one of text or image url must be provided")
public class MessageRequestDto {

    private String text;

    private String imageUrl;

    public Map<String, Object> getContent() {
        Map<String, Object> content = new HashMap<>();
        if (text != null && !text.isEmpty()) {
            content.put("text", text);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            content.put("image_url", imageUrl);
        }
        return content;
    }
}
