package com.hcmus.mela.user.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.request.CreateUserRequest;
import com.hcmus.mela.user.dto.request.UpdateUserRequest;
import com.hcmus.mela.user.dto.response.*;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.service.UserCommandService;
import com.hcmus.mela.user.service.UserQueryService;
import com.hcmus.mela.user.strategy.UserFilterStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, UserFilterStrategy> strategies;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping
    @Operation(tags = "👥 User Management Service", summary = "Get all users",
            description = "Get all users in the system")
    public ResponseEntity<GetUsersResponse> getAllUsers(
            @RequestParam(required = false) UserRole role,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Fetching all users");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID ownUserId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        UserFilterStrategy strategy = strategies.get("USER_" + userRole.toString().toUpperCase());
        GetUsersResponse response = userQueryService.getUsers(strategy, ownUserId, role);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(tags = "👥 User Management Service", summary = "Create user",
            description = "Create user information")
    public ResponseEntity<CreateUserResponse> createUserRequest(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create a user");
        UserDetailDto user = userCommandService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CreateUserResponse("User created successfully.", user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{userId}")
    @Operation(tags = "👥 User Management Service", summary = "Update user",
            description = "Update user information with user id")
    public ResponseEntity<UpdateUserResponse> updateUserRequest(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request) {
        log.info("Updating user with id {}", userId);
        userCommandService.updateUser(userId, request);
        return ResponseEntity.ok(new UpdateUserResponse("User updated successfully."));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    @Operation(tags = "👥 User Management Service", summary = "Delete user",
            description = "Delete user information with user id")
    public ResponseEntity<DeleteUserResponse> deleteUserRequest(
            @PathVariable UUID userId) {
        log.info("Deleting user with id {}", userId);
        userCommandService.deleteUser(userId);
        return ResponseEntity.ok(new DeleteUserResponse("User deleted successfully."));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping(value = "/{userId}")
    @Operation(tags = "👥 User Management Service", summary = "Get user information",
            description = "Get user information in the system")
    public ResponseEntity<GetUserDetailResponse> getUserInfo(
            @PathVariable UUID userId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Fetching user info for {}", userId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID ownUserId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        UserFilterStrategy strategy = strategies.get("USER_" + userRole.toString().toUpperCase());
        GetUserDetailResponse response = userQueryService.getUserInfo(strategy, ownUserId, userId);
        return ResponseEntity.ok(response);
    }
}
