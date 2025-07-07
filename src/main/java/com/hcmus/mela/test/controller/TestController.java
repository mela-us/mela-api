package com.hcmus.mela.test.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.test.dto.TestDto;
import com.hcmus.mela.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private JwtTokenService jwtTokenService;
    private TestService testService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(tags = "📝 Test Service", summary = "Get test",
            description = "Retrieve the test for the user in the system.")
    public ResponseEntity<TestDto> getTestRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        TestDto testDto = testService.getTestDto(userId);
        return ResponseEntity.ok(testDto);
    }
}
