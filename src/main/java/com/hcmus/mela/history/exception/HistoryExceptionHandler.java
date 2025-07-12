package com.hcmus.mela.history.exception;

import com.hcmus.mela.history.controller.ExerciseHistoryController;
import com.hcmus.mela.history.controller.LectureHistoryController;
import com.hcmus.mela.history.controller.TestHistoryController;
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
@RestControllerAdvice(basePackageClasses = {
        ExerciseHistoryController.class,
        LectureHistoryController.class,
        TestHistoryController.class
})
public class HistoryExceptionHandler {

    @ExceptionHandler(HistoryException.class)
    ResponseEntity<ApiErrorResponse> handleHistoryException(HistoryException exception, WebRequest request) {
        log.error("HistoryException occurred: {}", exception.getMessage(), exception);
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
