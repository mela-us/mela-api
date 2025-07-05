package com.hcmus.mela.media.controller;

import com.hcmus.mela.media.model.UploadType;
import com.hcmus.mela.shared.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/files")
public class MediaController {

    private final StorageService storageService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR', 'USER')")
    @GetMapping("/upload")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String name,
            @RequestHeader(required = false) String type
    ) {
        // UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        UploadType uploadType = UploadType.fromTypeName(type);
        name = UUID.randomUUID().toString().substring(5) + "_" + name;
        String path = String.format("%s%s", uploadType.getPath(), name);
        Map<String, String> urls = storageService.getUploadMelaFilePreSignedUrl(path);
        return ResponseEntity.ok().body(Map.of(
                "preSignedUrl", urls.get("preSignedUrl"),
                "fileUrl", urls.get("storedUrl"))
        );
    }
}
