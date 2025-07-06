package com.hcmus.mela.topic.exception;

import com.hcmus.mela.topic.controller.TopicController;
import com.hcmus.mela.shared.configuration.RequestIdFilter;
import com.hcmus.mela.shared.exception.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackageClasses = {
        TopicController.class,
})
public class TopicExceptionHandler {

    @ExceptionHandler(TopicException.class)
    ResponseEntity<ApiErrorResponse> handleMathContentException(TopicException exception, WebRequest request) {

        final ApiErrorResponse response = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
