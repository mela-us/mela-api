package com.hcmus.mela.level.exception;

import com.hcmus.mela.level.controller.LevelController;
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
        LevelController.class,
})
public class LevelExceptionHandler {

    @ExceptionHandler(LevelException.class)
    ResponseEntity<ApiErrorResponse> handleMathContentException(LevelException exception, WebRequest request) {

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
