package com.hcmus.mela.report.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
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
@RequestMapping("/api/reports")
public class ReportController {
//
//    @PreAuthorize("hasAnyAuthority('ADMIN", 'CONTRIBUTOR')")
//    @GetMapping("/upload")
}
