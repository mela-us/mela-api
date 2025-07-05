package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.GetConversationHistoryRequestDto;
import com.hcmus.mela.ai.chat.dto.request.GetListMessagesRequestDto;
import com.hcmus.mela.ai.chat.dto.response.ConversationInfoDto;
import com.hcmus.mela.ai.chat.dto.response.GetConversationHistoryResponseDto;
import com.hcmus.mela.ai.chat.dto.response.GetListMessagesResponseDto;

import java.util.UUID;

public interface ConversationHistoryService {
    GetConversationHistoryResponseDto getConversationHistory(GetConversationHistoryRequestDto request, UUID userId);

    ConversationInfoDto getConversation(UUID conversationId);

    GetListMessagesResponseDto getListMessages(GetListMessagesRequestDto request, UUID conversationId);

    void deleteConversationById(UUID conversationId);

    void deleteConversationByUserId(UUID userId);
}
