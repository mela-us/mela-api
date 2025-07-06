package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.CreateConversationRequestDto;
import com.hcmus.mela.ai.chat.dto.request.MessageRequestDto;
import com.hcmus.mela.ai.chat.dto.response.ChatResponseDto;
import com.hcmus.mela.ai.chat.model.Message;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    Object identifyProblem(Message message);

    Object resolveConfusion(List<Message> messageList, String context);

    Object reviewSubmission(List<Message> messageList, String context);

    Object provideSolution(List<Message> messageList, String context);

    ChatResponseDto getSolutionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto getReviewSubmissionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto sendMessage(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto createConversation(UUID userId, CreateConversationRequestDto createConversationRequestDto);
}
