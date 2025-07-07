package com.hcmus.mela.ai.chat.exception;

import com.hcmus.mela.ai.chat.controller.ConversationController;
import com.hcmus.mela.ai.chat.service.LectureConfusionServiceImpl;
import com.hcmus.mela.ai.chat.service.QuestionConfusionServiceImpl;
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
        ConversationController.class,
        LectureConfusionServiceImpl.class,
        QuestionConfusionServiceImpl.class
})
public class ChatBotExceptionHandler {

    @ExceptionHandler(ChatBotException.class)
    ResponseEntity<ApiErrorResponse> handleChatBotException(ChatBotException exception, WebRequest request) {
        log.error("ChatBotException occurred: {}", exception.getMessage());
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
