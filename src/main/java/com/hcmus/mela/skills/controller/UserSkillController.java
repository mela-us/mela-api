package com.hcmus.mela.skills.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.skills.dto.response.GetUserSkillResponse;
import com.hcmus.mela.skills.service.UserSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/users/skills")
public class UserSkillController {

    private final UserSkillService userSkillService;
    private final JwtTokenService jwtTokenService;

    @GetMapping
    @Operation(tags = "🎇 User skill Service", summary = "Get user's skills",
            description = "Retrieve a list of skills belonging to a user in their current level from the system")
    public ResponseEntity<GetUserSkillResponse> getUserSkill(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Getting user skills");
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        GetUserSkillResponse response = userSkillService.getUserSkillsByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
