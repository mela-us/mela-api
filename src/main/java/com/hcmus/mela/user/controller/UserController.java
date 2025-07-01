package com.hcmus.mela.user.controller;

import com.azure.core.annotation.Get;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.user.dto.request.*;
import com.hcmus.mela.user.dto.response.*;
import com.hcmus.mela.shared.storage.StorageService;
import com.hcmus.mela.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final StorageService storageService;

    private final JwtTokenService jwtTokenService;

    @RequestMapping(value = "/users/profile/upload-image-url", method = RequestMethod.GET)
    @Operation(
            tags = "User Service",
            description = "API endpoint to get pre-signed URL for uploading user profile image.")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extract user id from JWT token
        // File name will be user id
        UUID userId = jwtTokenService.getUserIdFromToken(
                jwtTokenService.extractTokenFromAuthorizationHeader(authorizationHeader)
        );

        // Get pre-signed URL for uploading user profile image
        final Map<String, String> urls = storageService.getUploadUserImagePreSignedUrl(userId.toString());


        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of("preSignedUrl", urls.get("preSignedUrl"), "imageUrl", urls.get("storedUrl"))
        );
    }

    @RequestMapping(value = "/users/profile", method = RequestMethod.PUT)
    @Operation(
            tags = "User Service",
            description = "API endpoint to update user profile.")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        final UpdateProfileResponse updateProfileResponse = userService.updateProfile(updateProfileRequest, authorizationHeader);

        return ResponseEntity.status(HttpStatus.OK).body(updateProfileResponse);
    }

    @RequestMapping(value = "/users/profile", method = RequestMethod.GET)
    @Operation(
            tags = "User Service",
            description = "API endpoint to get user profile.")
    public ResponseEntity<GetUserProfileResponse> getProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        final GetUserProfileResponse getUserProfileResponse = userService.getUserProfile(authorizationHeader);

        return ResponseEntity.status(HttpStatus.OK).body(getUserProfileResponse);
    }

    @RequestMapping(value = "/users/account", method = RequestMethod.DELETE)
    @Operation(
            tags = "User Service",
            description = "API endpoint to delete user account.")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @RequestBody @Valid DeleteAccountRequest deleteAccountRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        userService.deleteAccount(deleteAccountRequest, authorizationHeader);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "User account deleted successfully."));
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
//    @GetMapping(value = "/users")
//    public ResponseEntity<UserResponse> getAllUsers(
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
//        UserRequest userRequest = new UserRequest(userId);
//
//        final UserResponse userResponse = userService.getAllUsers(userRequest);
//
//        return ResponseEntity.ok(userResponse);
//    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
//    @GetMapping(value = "/users/scores")
//    public ResponseEntity<UserScoresResponse> getUserScores(
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
//        UserScoresRequest userScoresRequest = new UserScoresRequest(userId);
//
//        final UserScoresResponse userScoresResponse = userService.getUserScores(userScoresRequest);
//
//        return ResponseEntity.ok(userScoresResponse);
//    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
//    @GetMapping(value = "/users/{userId}/profile")
//    public ResponseEntity<GetUserProfileResponse> getUserProfile(
//            @PathVariable String userId,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        final GetUserProfileResponse getUserProfileResponse = userService.getUserProfileById(UUID.fromString(userId), authorizationHeader);
//
//        return ResponseEntity.status(HttpStatus.OK).body(getUserProfileResponse);
//    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
//    @GetMapping(value = "/users/{userId}/report")
//    public ResponseEntity<UserReportResponse> getUserReport(
//            @PathVariable String userId,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        final UserReportResponse userReportResponse = userService.getUserReport(UUID.fromString(userId), authorizationHeader);
//
//        return ResponseEntity.status(HttpStatus.OK).body(userReportResponse);
//    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @DeleteMapping(value = "/users/{userId}")
//    public ResponseEntity<Map<String, String>> deleteUser(
//            @PathVariable String userId,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        userService.deleteUser(UUID.fromString(userId), authorizationHeader);
//
//        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "User deleted successfully."));
//    }
//
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @PutMapping(value = "/users/{userId}")
//    public ResponseEntity<UpdateUserResponse> updateUser(
//            @PathVariable String userId,
//            @RequestBody @Valid UpdateUserRequest updateUserRequest,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        final UpdateUserResponse updateUserResponse = userService.updateUser(UUID.fromString(userId), updateUserRequest, authorizationHeader);
//
//        return ResponseEntity.status(HttpStatus.OK).body(updateUserResponse);
//    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @PostMapping(value = "/users")
//    public ResponseEntity<CreateUserResponse> createUser(
//            @RequestBody @Valid CreateUserRequest createUserRequest,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        final CreateUserResponse createUserResponse = userService.createUser(createUserRequest, authorizationHeader);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(createUserResponse);
//    }
}
