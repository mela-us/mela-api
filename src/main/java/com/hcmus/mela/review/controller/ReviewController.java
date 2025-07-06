package com.hcmus.mela.review.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.dto.response.GetReviewsResponse;
import com.hcmus.mela.review.dto.response.UpdateReviewResponse;
import com.hcmus.mela.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(tags = "🔃 Review Service", summary = "Get reviews",
            description = "Retrieves all reviews of the user with given user id.")
    public ResponseEntity<GetReviewsResponse> getReviews(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Get review sections and exercises for user {}", userId);
        GetReviewsResponse response = reviewService.getReviews(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{reviewId}")
    @Operation(tags = "🔃 Review Service", summary = "Update Review",
            description = "Updates the review with the given id for the user.")
    public ResponseEntity<UpdateReviewResponse> updateReview(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable("reviewId") String reviewId,
            @RequestBody UpdateReviewRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Update review {} for user {}", reviewId, userId);
        UpdateReviewResponse response = reviewService.updateReview(userId, UUID.fromString(reviewId), request);
        return ResponseEntity.ok(response);
    }
}
