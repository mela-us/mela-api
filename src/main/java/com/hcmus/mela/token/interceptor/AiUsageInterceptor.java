package com.hcmus.mela.token.interceptor;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.shared.exception.BadRequestException;
import com.hcmus.mela.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AiUsageInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final JwtTokenService jwtTokenService;

    private static final int TOKEN_COST = 1;

    private final ThreadLocal<UUID> currentUserId = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);

        if (!tokenService.validateUserToken(userId)) {
            throw new BadRequestException("Not enough token");
        }

        currentUserId.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UUID userId = currentUserId.get();
        if (userId != null && response.getStatus() >= 200 && response.getStatus() < 300) {
            tokenService.reduceUserToken(userId, TOKEN_COST);
        }
        currentUserId.remove();
    }
}
