package com.hcmus.mela.media.controller;

import com.hcmus.mela.media.dto.GetUploadUrlResponse;
import com.hcmus.mela.media.model.UploadType;
import com.hcmus.mela.shared.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/files")
public class MediaController {

    private final StorageService storageService;

    @GetMapping("/upload")
    @Operation(tags = "📷 Media Service", summary = "Get upload URL",
            description = "Get pre-signed URL for uploading files to the storage service.")
    public ResponseEntity<GetUploadUrlResponse> getUploadUrl(
            @RequestParam(required = false) String name,
            @RequestHeader(required = false) UploadType type) {
        name = UUID.randomUUID().toString().substring(3) + "_" + name;
        String path = String.format("%s%s", type.getPath(), name);
        Map<String, String> urls = storageService.getUploadMelaFilePreSignedUrl(path);
        return ResponseEntity.ok(new GetUploadUrlResponse(
                urls.get("preSignedUrl"),
                urls.get("storedUrl")
        ));
    }
}
