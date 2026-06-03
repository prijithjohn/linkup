package com.linkup.ai.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/linkedin")
@CrossOrigin(origins = "*")
public class LinkedInController {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInController.class);
    private final LinkedInAIService service;

    public LinkedInController(LinkedInAIService service) {
        this.service = service;
    }

   
    @PostMapping(value = "/fast-reply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedInResponse> fastReply(@RequestBody(required = false) LinkedInRequest request) {
        logRequest(request);
        if (isInvalidMessageContent(request)) {
            return ResponseEntity.ok(invalidInputResponse(request));
        }

        request.setAction(AIConstants.ACTION_FAST_REPLY);
        LinkedInResponse response = service.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }

 
    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedInResponse> generate(@RequestBody(required = false) LinkedInRequest request) {
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
