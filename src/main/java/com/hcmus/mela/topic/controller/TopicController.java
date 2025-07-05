package com.hcmus.mela.topic.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.DenyTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.service.TopicCommandService;
import com.hcmus.mela.topic.service.TopicQueryService;
import com.hcmus.mela.topic.service.TopicStatusService;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR', 'STUDENT')")
    @GetMapping
    @Operation(
            tags = "Topic Service",
            summary = "Get all topics",
            description = "Get all topics existing in the system."
    )
    public ResponseEntity<GetTopicsResponse> getTopicsRequest(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting topics in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString());
        GetTopicsResponse response = topicQueryService.getTopicsResponse(strategy, userId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    public ResponseEntity<CreateTopicResponse> createTopicRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateTopicRequest request) {
        log.info("Creating topic {}", request.getName());
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        CreateTopicResponse response = topicCommandService.createTopic(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping("/{topicId}")
    public ResponseEntity<Map<String, String>> updateTopicRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateTopicRequest request,
            @PathVariable UUID topicId) {
        log.info("Updating topic {}", topicId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString().toUpperCase());
        topicCommandService.updateTopic(strategy, userId, topicId, request);
        return ResponseEntity.ok(Map.of("message", "Topic updated successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Map<String, String>> deleteTopicRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID topicId) {
        log.info("Delete topic {}", topicId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString().toUpperCase());
        topicCommandService.deleteTopic(strategy, userId, topicId);
        return ResponseEntity.ok(Map.of("message", "Topic deleted successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{topicId}/deny")
    public ResponseEntity<Map<String, String>> denyTopicRequest(
            @PathVariable UUID topicId,
            @RequestBody DenyTopicRequest request) {
        log.info("Deny topic {}", topicId);
        topicStatusService.denyTopic(topicId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Topic denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{topicId}/approve")
    public ResponseEntity<Map<String, String>> approveTopicRequest(@PathVariable UUID topicId) {
        log.info("Approve topic {}", topicId);
        topicStatusService.approveTopic(topicId);
        return ResponseEntity.ok(Map.of("message", "Topic approved successfully"));
    }
}
