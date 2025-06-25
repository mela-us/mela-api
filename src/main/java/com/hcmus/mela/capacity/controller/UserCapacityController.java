package com.hcmus.mela.capacity.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.capacity.dto.response.GetUserCapacityResponse;
import com.hcmus.mela.capacity.service.UserCapacityService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/capacities")
public class UserCapacityController {
    private final UserCapacityService userCapacityService;


    @GetMapping("")
    @Operation(tags = "User Capacity Service",
            summary = "Get user's capacities",
            description = "Retrieve a list of capacities belonging to a user in their current level from the system")
    public ResponseEntity<GetUserCapacityResponse> getUserCapacity(@RequestHeader("Authorization") String authorizationHeader) {
        GetUserCapacityResponse response = userCapacityService.getUserCapacity(authorizationHeader);

        return ResponseEntity.ok(response);
    }
}
