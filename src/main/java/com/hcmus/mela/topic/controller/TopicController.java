package com.hcmus.mela.topic.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.DenyTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.*;
import com.hcmus.mela.topic.service.TopicCommandService;
import com.hcmus.mela.topic.service.TopicQueryService;
import com.hcmus.mela.topic.service.TopicStatusService;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicQueryService topicQueryService;
    private final TopicStatusService topicStatusService;
    private final TopicCommandService topicCommandService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, TopicFilterStrategy> strategies;

    @GetMapping
    @Operation(tags = "🎈 Topic Service", summary = "Get all topics",
            description = "Get all topics existing in the system.")
    public ResponseEntity<GetTopicsResponse> getTopicsRequest(
            @Parameter(hidden = true) @Valid @RequestHeader("Authorization") String authHeader) {
        log.info("Getting topics in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString());
        GetTopicsResponse response = topicQueryService.getTopicsResponse(strategy, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(tags = "🎈 Topic Service", summary = "Create a new topic",
            description = "Create a new topic in the system.")
    public ResponseEntity<CreateTopicResponse> createTopicRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateTopicRequest request) {
        log.info("Creating topic {}", request.getName());
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString());
        CreateTopicResponse response = topicCommandService.createTopic(strategy, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{topicId}")
    @Operation(tags = "🎈 Topic Service", summary = "Update an existing topic",
            description = "Update an existing topic in the system.")
    public ResponseEntity<UpdateTopicResponse> updateTopicRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateTopicRequest request,
            @PathVariable UUID topicId) {
        log.info("Updating topic {}", topicId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString().toUpperCase());
        topicCommandService.updateTopic(strategy, userId, topicId, request);
        return ResponseEntity.ok(new UpdateTopicResponse("Topic updated successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{topicId}")
    @Operation(tags = "🎈 Topic Service", summary = "Delete an existing topic",
            description = "Delete an existing topic in the system.")
    public ResponseEntity<DeleteTopicResponse> deleteTopicRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID topicId) {
        log.info("Delete topic {}", topicId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString().toUpperCase());
        topicCommandService.deleteTopic(strategy, userId, topicId);
        return ResponseEntity.ok(new DeleteTopicResponse("Topic deleted successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{topicId}/deny")
    @Operation(tags = "🎈 Topic Service", summary = "Deny a topic request",
            description = "Deny a topic request with a reason.")
    public ResponseEntity<DenyTopicResponse> denyTopicRequest(
            @PathVariable UUID topicId,
            @RequestBody DenyTopicRequest request) {
        log.info("Deny topic {}", topicId);
        if (request.getReason() == null || request.getReason().isEmpty()) {
            request.setReason("Liên hệ quản trị viên để biết thêm chi tiết");
        }
        topicStatusService.denyTopic(topicId, request.getReason());
        return ResponseEntity.ok(new DenyTopicResponse("Topic denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{topicId}/approve")
    @Operation(tags = "🎈 Topic Service", summary = "Approve a topic request",
            description = "Approve a topic request.")
    public ResponseEntity<ApproveTopicResponse> approveTopicRequest(@PathVariable UUID topicId) {
        log.info("Approve topic {}", topicId);
        topicStatusService.approveTopic(topicId);
        return ResponseEntity.ok(new ApproveTopicResponse("Topic approved successfully"));
    }
}
