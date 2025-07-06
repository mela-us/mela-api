package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.CreateConversationRequestDto;
import com.hcmus.mela.ai.chat.dto.request.GetConversationHistoryRequestDto;
import com.hcmus.mela.ai.chat.dto.request.GetListMessagesRequestDto;
import com.hcmus.mela.ai.chat.dto.request.MessageRequestDto;
import com.hcmus.mela.ai.chat.dto.response.*;
import com.hcmus.mela.ai.chat.service.ConversationHistoryService;
import com.hcmus.mela.ai.chat.service.ConversationService;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.shared.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final JwtTokenService jwtTokenService;
    private final StorageService storageService;
    private final ConversationHistoryService conversationHistoryService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(tags = "💬 Conversation Service", summary = "Create conversation",
            description = "Create a new conversation for the user.")
    public ResponseEntity<ChatResponseDto> createConversation(
            @Valid @RequestBody CreateConversationRequestDto request,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ChatResponseDto chatResponseDto = conversationService.createConversation(userId, request);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages")
    @Operation(tags = "💬 Conversation Service", summary = "Send message",
            description = "Send a message to the conversation.")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @Valid @RequestBody MessageRequestDto request,
            @PathVariable String conversationId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ChatResponseDto chatResponseDto = conversationService
                .sendMessage(request, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages/review-submission")
    @Operation(tags = "💬 Conversation Service", summary = "Review submission",
            description = "Review a submission in the conversation.")
    public ResponseEntity<ChatResponseDto> reviewSubmission(
            @Valid @RequestBody MessageRequestDto request,
            @PathVariable String conversationId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ChatResponseDto chatResponseDto = conversationService
                .getReviewSubmissionResponse(request, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages/solution")
    @Operation(tags = "💬 Conversation Service", summary = "Get solution",
            description = "Review a submission in the conversation.")
    public ResponseEntity<ChatResponseDto> getSolution(
            @Valid @RequestBody MessageRequestDto request,
            @PathVariable String conversationId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ChatResponseDto chatResponseDto = conversationService
                .getSolutionResponse(request, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/files/upload-url")
    @Operation(tags = "💬 Conversation Service", summary = "Upload file",
            description = "Get pre-signed URL for file upload.")
    public ResponseEntity<GetUploadPreSignedUrlResponse> getUploadUrl() {
        Map<String, String> urls = storageService.getUploadConversationFilePreSignedUrl(UUID.randomUUID().toString());
        GetUploadPreSignedUrlResponse response = new GetUploadPreSignedUrlResponse(
                urls.get("preSignedUrl"), urls.get("storedUrl")
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(tags = "💬 Conversation Service", summary = "Get conversation history",
            description = "Get the conversation history for the user.")
    public ResponseEntity<GetConversationHistoryResponseDto> getConversationHistory(
            @Valid @RequestBody GetConversationHistoryRequestDto request,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        GetConversationHistoryResponseDto conversationHistoryResponseDto = conversationHistoryService
                .getConversationHistory(request, userId);
        return ResponseEntity.ok(conversationHistoryResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{conversationId}")
    @Operation(tags = "💬 Conversation Service", summary = "Get conversation info",
            description = "Get detailed information about a specific conversation.")
    public ResponseEntity<ConversationInfoDto> getConversation(@PathVariable String conversationId) {
        ConversationInfoDto conversationInfoDto = conversationHistoryService.getConversation(UUID.fromString(conversationId));
        return ResponseEntity.ok(conversationInfoDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{conversationId}/messages")
    @Operation(tags = "💬 Conversation Service", summary = "Get messages in conversation",
            description = "Get a list of messages in a specific conversation.")
    public ResponseEntity<GetListMessagesResponseDto> getListMessages(
            @Valid @RequestBody GetListMessagesRequestDto request,
            @PathVariable String conversationId) {
        GetListMessagesResponseDto messagesResponseDto = conversationHistoryService
                .getListMessages(request, UUID.fromString(conversationId));
        return ResponseEntity.ok(messagesResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{conversationId}")
    @Operation(tags = "💬 Conversation Service", summary = "Delete conversation",
            description = "Delete a specific conversation by its id.")
    public ResponseEntity<Void> deleteConversation(@PathVariable String conversationId) {
        conversationHistoryService.deleteConversationById(UUID.fromString(conversationId));
        return ResponseEntity.noContent().build();
    }
}
