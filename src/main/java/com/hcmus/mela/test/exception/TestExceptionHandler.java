package com.hcmus.mela.test.exception;

import com.hcmus.mela.shared.configuration.RequestIdFilter;
import com.hcmus.mela.shared.exception.ApiErrorResponse;
import com.hcmus.mela.test.controller.TestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = TestController.class)
public class TestExceptionHandler {
    @ExceptionHandler(QuestionResultEmptyException.class)
    ResponseEntity<ApiErrorResponse> handleQuestionResultEmptyException(QuestionResultEmptyException ex, WebRequest request) {
        final ApiErrorResponse response = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
