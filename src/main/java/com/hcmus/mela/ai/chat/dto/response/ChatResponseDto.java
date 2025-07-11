package com.hcmus.mela.ai.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ChatResponseDto {

    private UUID conversationId;

    private String title;

    private List<MessageResponseDto> message;

    private ConversationMetadataDto metadata;
}
