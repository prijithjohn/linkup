package com.linkup.ai.app;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LinkedInController {

    private final LinkedInAIService service;

    public LinkedInController(LinkedInAIService service) {
        this.service = service;
    }

    // =========================
    // 🔵 LEGACY ENDPOINT (DO NOT REMOVE)
    // =========================
    @PostMapping("/email/generate")
    public LinkedInResponse legacyGenerate(@Valid @RequestBody LinkedInRequest request) {
        // Keeps your React app + old extension working
        return service.generateEmailReply(request);
    }

    // =========================
    // ⚡ FAST LINKEDIN MODE (EXTENSION)
    // =========================
    @PostMapping("/linkedin/fast-reply")
    public LinkedInResponse fastReply(@RequestBody LinkedInRequest request) {
        request.setAction("FAST_REPLY");
        return service.generateEmailReply(request);
    }

    // =========================
    // 🧠 QUALITY LINKEDIN MODE (WEB APP)
    // =========================
    @PostMapping("/linkedin/generate")
    public LinkedInResponse generate(@RequestBody LinkedInRequest request) {
        request.setAction("QUALITY_REPLY");
        return service.generateEmailReply(request);
    }
}