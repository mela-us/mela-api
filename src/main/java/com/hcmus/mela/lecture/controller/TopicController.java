package com.hcmus.mela.lecture.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.lecture.dto.request.CreateTopicRequest;
import com.hcmus.mela.lecture.dto.response.CreateTopicResponse;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.service.TopicService;
import com.hcmus.mela.lecture.strategy.TopicFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
@Slf4j
public class TopicController {

    private final TopicService topicService;

    private final JwtTokenService jwtTokenService;

    private final Map<String, TopicFilterStrategy> strategies;

    @GetMapping
    @Operation(
            tags = "Math Category Service",
            summary = "Get all topics",
            description = "Get all topics existing in the system."
    )
    public ResponseEntity<GetTopicsResponse> getTopicsRequest(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting topics in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        TopicFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString());
        GetTopicsResponse response = topicService.getTopicsResponse(strategy, userId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    public ResponseEntity<CreateTopicResponse> createTopicRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateTopicRequest createTopicRequest) {
        log.info("Creating topic");
        UUID creatorId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        CreateTopicResponse response = topicService.getCreateTopicResponse(creatorId, createTopicRequest);

        return ResponseEntity.ok(response);
    }
}
