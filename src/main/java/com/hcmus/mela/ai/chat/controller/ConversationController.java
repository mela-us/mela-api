package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.CreateConversationRequestDto;
import com.hcmus.mela.ai.chat.dto.request.GetConversationHistoryRequestDto;
import com.hcmus.mela.ai.chat.dto.request.GetListMessagesRequestDto;
import com.hcmus.mela.ai.chat.dto.request.MessageRequestDto;
import com.hcmus.mela.ai.chat.dto.response.ChatResponseDto;
import com.hcmus.mela.ai.chat.dto.response.ConversationInfoDto;
import com.hcmus.mela.ai.chat.dto.response.GetConversationHistoryResponseDto;
import com.hcmus.mela.ai.chat.dto.response.GetListMessagesResponseDto;
import com.hcmus.mela.ai.chat.service.ConversationHistoryService;
import com.hcmus.mela.ai.chat.service.ConversationService;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.shared.storage.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    private final JwtTokenService jwtTokenService;
    private final StorageService storageService;
    private final ConversationHistoryService conversationHistoryService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("")
    public ResponseEntity<ChatResponseDto> createConversation(
            @Valid @RequestBody CreateConversationRequestDto createConversationRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        ChatResponseDto chatResponseDto = conversationService.createConversation(userId, createConversationRequestDto);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @Valid @RequestBody MessageRequestDto messageRequestDto,
            @PathVariable String conversationId,
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        ChatResponseDto chatResponseDto = conversationService
                .sendMessage(messageRequestDto, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages/review-submission")
    public ResponseEntity<ChatResponseDto> reviewSubmission(
            @Valid @RequestBody MessageRequestDto messageRequestDto,
            @PathVariable String conversationId,
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        ChatResponseDto chatResponseDto = conversationService
                .getReviewSubmissionResponse(messageRequestDto, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{conversationId}/messages/solution")
    public ResponseEntity<ChatResponseDto> getSolution(
            @Valid @RequestBody MessageRequestDto messageRequestDto,
            @PathVariable String conversationId,
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        ChatResponseDto chatResponseDto = conversationService
                .getSolutionResponse(messageRequestDto, UUID.fromString(conversationId), userId);
        return ResponseEntity.ok(chatResponseDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/files/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl() {
        Map<String, String> urls = storageService.getUploadConversationFilePreSignedUrl(UUID.randomUUID().toString());

        return ResponseEntity.ok().body(
                Map.of("preSignedUrl", urls.get("preSignedUrl"), "fileUrl", urls.get("storedUrl"))
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("")
    public ResponseEntity<GetConversationHistoryResponseDto> getConversationHistory(
            @Valid @RequestBody GetConversationHistoryRequestDto request,
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        return ResponseEntity.ok(conversationHistoryService.getConversationHistory(request, userId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationInfoDto> getConversation(@PathVariable String conversationId) {
        ConversationInfoDto conversationInfoDto = conversationHistoryService.getConversation(UUID.fromString(conversationId));
        return ResponseEntity.ok(conversationInfoDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<GetListMessagesResponseDto> getListMessages(
            @Valid @RequestBody GetListMessagesRequestDto request,
            @PathVariable String conversationId) {
        return ResponseEntity.ok(conversationHistoryService.getListMessages(request, UUID.fromString(conversationId)));
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String conversationId) {
        conversationHistoryService.deleteConversation(UUID.fromString(conversationId));
        return ResponseEntity.noContent().build();
    }
}
