package com.hcmus.mela.user.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.shared.storage.StorageService;
import com.hcmus.mela.user.dto.request.DeleteProfileRequest;
import com.hcmus.mela.user.dto.request.UpdateProfileRequest;
import com.hcmus.mela.user.dto.response.DeleteProfileResponse;
import com.hcmus.mela.user.dto.response.GetProfileResponse;
import com.hcmus.mela.user.dto.response.GetUploadUrlResponse;
import com.hcmus.mela.user.dto.response.UpdateProfileResponse;
import com.hcmus.mela.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserProfileController {

    private final ProfileService profileService;
    private final StorageService storageService;
    private final JwtTokenService jwtTokenService;

    @GetMapping(value = "/profile/upload-image-url")
    @Operation(tags = "👤 User Profile Service", summary = "Get upload URL",
            description = "API endpoint to get pre-signed URL for uploading user profile image.")
    public ResponseEntity<GetUploadUrlResponse> getUploadUrlRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        final Map<String, String> urls = storageService.getUploadUserImagePreSignedUrl(userId.toString());
        log.info("Generated pre-signed URL for user {}", userId);
        return ResponseEntity.ok(new GetUploadUrlResponse(
                urls.get("preSignedUrl"),
                urls.get("storedUrl")
        ));
    }

    @PutMapping(value = "/profile")
    @Operation(tags = "👤 User Profile Service", summary = "Upload user profile",
            description = "API endpoint to update user profile.")
    public ResponseEntity<UpdateProfileResponse> updateProfileRequest(
            @RequestBody @Valid UpdateProfileRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Updating profile for user {}", userId);
        profileService.updateProfile(userId, request);
        return ResponseEntity.ok(new UpdateProfileResponse("User profile updated successfully."));
    }

    @GetMapping(value = "/profile")
    @Operation(tags = "👤 User Profile Service", summary = "Get user profile",
            description = "API endpoint to get user profile.")
    public ResponseEntity<GetProfileResponse> getProfileRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Fetching profile for user {}", userId);
        GetProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(value = "/account")
    @Operation(tags = "👤 User Profile Service", summary = "Delete user profile",
            description = "API endpoint to delete user account.")
    public ResponseEntity<DeleteProfileResponse> deleteAccountRequest(
            @RequestBody @Valid DeleteProfileRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Deleting account for user {}", userId);
        profileService.deleteProfile(userId, request);
        return ResponseEntity.ok(new DeleteProfileResponse("User account deleted successfully."));
    }
}