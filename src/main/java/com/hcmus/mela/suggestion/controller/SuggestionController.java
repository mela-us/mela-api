package com.hcmus.mela.suggestion.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.suggestion.dto.request.UpdateSuggestionRequest;
import com.hcmus.mela.suggestion.dto.response.GetSuggestionsResponse;
import com.hcmus.mela.suggestion.dto.response.UpdateSuggestionResponse;
import com.hcmus.mela.suggestion.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suggestions")
@Slf4j
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final JwtTokenService jwtTokenService;

    @GetMapping
    @Operation(
            tags = "Suggestion Service",
            summary = "Get suggestions by user ID",
            description = "Retrieves all suggestions of the user with given user ID."
    )
    public ResponseEntity<GetSuggestionsResponse> getSuggestions(@RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Get suggestion sections for user {}", userId);
        GetSuggestionsResponse response = suggestionService.getSuggestions(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{suggestionId}")
    @Operation(
            tags = "Suggestion Service",
            summary = "Update suggestion with suggestion ID",
            description = "Update suggestion that has given ID."
    )
    public ResponseEntity<UpdateSuggestionResponse> updateSuggestion(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("suggestionId") String suggestionId,
            @RequestBody UpdateSuggestionRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Update suggestion with suggestion id {} for user {}", suggestionId, userId);
        UpdateSuggestionResponse response = suggestionService.updateSuggestion(userId, UUID.fromString(suggestionId), request);
        return ResponseEntity.ok(response);
    }
}
