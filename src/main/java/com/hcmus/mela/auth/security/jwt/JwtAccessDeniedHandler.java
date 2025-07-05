package com.hcmus.mela.auth.security.jwt;

import com.hcmus.mela.shared.exception.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
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
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        log.error("Access denied for request {}, {}", request.getRequestURI(), exception.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                getRequestId(),
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
        response.getWriter().write(apiErrorResponse.toJson());
    }
}
