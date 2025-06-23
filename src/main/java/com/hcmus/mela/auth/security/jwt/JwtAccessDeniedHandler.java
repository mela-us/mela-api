package com.hcmus.mela.auth.security.jwt;

import com.hcmus.mela.shared.exception.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private String getRequestId() {
        String requestId = MDC.get("X-Request-Id");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                getRequestId(),
                HttpStatus.FORBIDDEN.value(),
                accessDeniedException.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        response.getWriter().write(apiErrorResponse.toJson());
    }
}
