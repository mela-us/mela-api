package com.hcmus.mela.review.exception;

import com.hcmus.mela.review.controller.ReviewController;
import com.hcmus.mela.shared.configuration.RequestIdFilter;
import com.hcmus.mela.shared.exception.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = ReviewController.class)
public class ReviewExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleReviewNotFoundException(ReviewNotFoundException ex, WebRequest request) {
        log.error("ReviewNotFoundException occurred: {}", ex.getMessage());
        final ApiErrorResponse response = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
