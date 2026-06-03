package com.linkup.ai.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


@Service
public class LinkedInAIService {
    private static final Logger logger = LoggerFactory.getLogger(LinkedInAIService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    public LinkedInAIService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

   
    public LinkedInResponse generateEmailReply(LinkedInRequest request) {
        String context = sanitizeContext(request.getMessageContent());
        String tone = sanitizeTone(request.getTone());
        String action = normalizeAction(request.getAction());

        boolean fastMode = AIConstants.ACTION_FAST_REPLY.equals(action);

        String prompt = buildPrompt(action, tone, context, request, fastMode);
        LinkedInResponse response = callGroqAPI(prompt, action, tone, fastMode);

        return response != null ? response
                : new LinkedInResponse(AIConstants.FALLBACK_REPLY, tone, action);
    }

    /**
     * Build optimized prompt using minimal token structure.
     */
    private String buildPrompt(String action, String tone, String context, LinkedInRequest request, boolean fastMode) {
        if (fastMode) {
            return PromptBuilder.buildFastPrompt(tone, context);
        }

        return PromptBuilder.buildPrompt(
                action,
                tone,
                context,
                request.getRecipientName(),
                request.getTargetRole(),
                request.getTargetCompany()
        );
    }

    /**
     * Call Groq API with minimal token configuration.
     */
    private LinkedInResponse callGroqAPI(String prompt, String action, String tone, boolean fastMode) {
        try {
            Map<String, Object> body = buildRequestBody(prompt, fastMode);
            logRequest(prompt);

            String response = webClient.post()
                    .uri(groqApiUrl)
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String reply = extractResponse(response);

            if (reply == null || reply.isBlank()) {
                logger.warn("Empty AI response received");
                return new LinkedInResponse(AIConstants.FALLBACK_REPLY, tone, action);
            }

            return new LinkedInResponse(reply, tone, action);

        } catch (Exception e) {
            logger.error("Groq API call failed: {}", e.getClass().getSimpleName());
            return new LinkedInResponse(AIConstants.FALLBACK_REPLY, tone, action);
        }
    }

    /**
     * Build request body for Groq API.
     */
    private Map<String, Object> buildRequestBody(String prompt, boolean fastMode) {
        return Map.of(
                "model", AIConstants.GROQ_MODEL,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", fastMode ? AIConstants.FAST_MODE_TEMPERATURE : AIConstants.QUALITY_MODE_TEMPERATURE,
                "max_tokens", fastMode ? AIConstants.FAST_MODE_MAX_TOKENS : AIConstants.QUALITY_MODE_MAX_TOKENS
        );
    }

    /**
     * Extract reply content from Groq API JSON response.
     * Safe fallback handling for malformed or null responses.
     */
    private String extractResponse(String response) {
        if (response == null || response.isBlank()) {
            logger.warn("Null response from API");
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path(AIConstants.RESPONSE_PATH_CHOICES);

            if (choices.isEmpty()) {
                logger.warn("No choices in API response");
                return null;
            }

            String content = choices.get(0)
                    .path(AIConstants.RESPONSE_PATH_MESSAGE)
                    .path(AIConstants.RESPONSE_PATH_CONTENT)
                    .asText();

            return content != null ? content.trim() : null;

        } catch (Exception e) {
            logger.error("Failed to parse API response: {}", e.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * Sanitize and validate context input.
     */
    private String sanitizeContext(String context) {
        if (context == null || context.isBlank()) {
            return "";
        }

        context = context.trim();
        if (context.length() > AIConstants.MAX_CONTEXT_LENGTH) {
            context = context.substring(0, AIConstants.MAX_CONTEXT_LENGTH);
        }

        return context;
    }

    /**
     * Sanitize tone input.
     */
    private String sanitizeTone(String tone) {
        if (tone == null || tone.isBlank()) {
            return AIConstants.DEFAULT_TONE;
        }
        return tone.trim();
    }

    /**
     * Normalize action to match AIConstants values.
     */
    private String normalizeAction(String action) {
        if (action == null || action.isBlank()) {
            return AIConstants.DEFAULT_ACTION;
        }

        String normalized = action.toUpperCase().replace(" ", "_");

        return normalized.equals(AIConstants.ACTION_FAST_REPLY)
                || normalized.equals(AIConstants.ACTION_QUALITY_REPLY)
                || normalized.equals(AIConstants.ACTION_REFERRAL)
                || normalized.equals(AIConstants.ACTION_COLD_PITCH)
                || normalized.equals(AIConstants.ACTION_CONNECTION)
                ? normalized
                : AIConstants.DEFAULT_ACTION;
    }

    /**
     * Log prompt (without API key) for debugging.
     */
    private void logRequest(String prompt) {
        logger.debug("Groq API prompt: {} tokens (approx)", prompt.split("\\s+").length);
    }
}