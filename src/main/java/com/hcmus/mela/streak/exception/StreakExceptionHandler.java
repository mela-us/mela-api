package com.hcmus.mela.streak.exception;

import com.hcmus.mela.shared.configuration.RequestIdFilter;
import com.hcmus.mela.shared.exception.ApiErrorResponse;
import com.hcmus.mela.streak.controller.StreakController;
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
@RestControllerAdvice(basePackageClasses = StreakController.class)
public class StreakExceptionHandler {

    @ExceptionHandler(StreakException.class)
    ResponseEntity<ApiErrorResponse> handleStreakException(StreakException exception, WebRequest request) {
        log.error("StreakException occurred: {}", exception.getMessage());
        final ApiErrorResponse response = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
