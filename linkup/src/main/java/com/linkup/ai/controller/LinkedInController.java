package com.linkup.ai.controller;

import com.linkup.ai.dto.LinkedInRequest;
import com.linkup.ai.dto.LinkedInResponse;
import com.linkup.ai.service.LinkedInAIService;
import com.linkup.ai.util.AIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * REST API endpoints for LinkedIn AI reply generation.
 * All endpoints return consistent JSON format:
 * {
 *   "reply": string,
 *   "tone": string,
 *   "action": string
 * }
 */
@RestController
@RequestMapping("/api/linkedin")
@CrossOrigin(origins = "*")
public class LinkedInController {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInController.class);
    private final LinkedInAIService service;

    public LinkedInController(LinkedInAIService service) {
        this.service = service;
    }

    /**
     * Legacy endpoint for React app and Chrome extension.
     * Generates email/LinkedIn reply with flexible action routing.
     */
  

    /**
     * Fast-reply endpoint (Chrome extension use case).
     * Optimized for speed: ~2 sentences, lower temperature, fewer tokens.
     */
    @PostMapping(value = "/fast-reply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedInResponse> fastReply(@Valid @RequestBody(required = false) LinkedInRequest request) {
        logRequest(request);
        if (isInvalidMessageContent(request)) {
            return ResponseEntity.ok(invalidInputResponse(request));
        }

        request.setAction(AIConstants.ACTION_FAST_REPLY);
        LinkedInResponse response = service.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Quality-reply endpoint (Web app use case).
     * Optimized for quality: thoughtful message, higher temperature, more tokens.
     */
    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedInResponse> generate(@Valid @RequestBody(required = false) LinkedInRequest request) {
        logRequest(request);
        if (isInvalidMessageContent(request)) {
            return ResponseEntity.ok(invalidInputResponse(request));
        }

        request.setAction(AIConstants.ACTION_QUALITY_REPLY);
        LinkedInResponse response = service.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }

    private boolean isInvalidMessageContent(LinkedInRequest request) {
        return request == null || request.getMessageContent() == null || request.getMessageContent().isBlank();
    }

    private LinkedInResponse invalidInputResponse(LinkedInRequest request) {
        String action = AIConstants.DEFAULT_ACTION;
        if (request != null && request.getAction() != null && !request.getAction().isBlank()) {
            action = request.getAction().trim();
        }

        return new LinkedInResponse(
                "Invalid input: message content is required",
                "N/A",
                action
        );
    }

    private void logRequest(LinkedInRequest request) {
        int length = request == null || request.getMessageContent() == null ? 0 : request.getMessageContent().length();
        String tone = request == null ? null : request.getTone();
        String action = request == null ? null : request.getAction();
        logger.debug("Incoming request: messageContentLength={}, tone={}, action={}", length, tone, action);
    }
}