package com.hcmus.mela.test.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.test.dto.TestDto;
import com.hcmus.mela.test.repository.TestQuestionRepository;
import com.hcmus.mela.test.service.TestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/tests")
public class TestController {
    private JwtTokenService jwtTokenService;
    private TestService testService;

    @GetMapping(value = "")
    public ResponseEntity<TestDto> getTest(
            @RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        TestDto testDto = testService.getTestDto(userId);
        return ResponseEntity.ok(testDto);
    }
}
