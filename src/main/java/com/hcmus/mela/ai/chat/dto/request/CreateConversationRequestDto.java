package com.hcmus.mela.ai.chat.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConversationRequestDto {

    @Valid
    @NotNull(message = "Message cannot be null")
    private MessageRequestDto message;
}
