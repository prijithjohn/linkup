package com.linkup.ai.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal token-efficient prompt builder for Groq API.
 * Structures prompts as: TASK | TONE | INPUT | OUTPUT_RULES
 * Target: 80-150 tokens per prompt
 */
public class PromptBuilder {

    private static final Map<String, String> TASK_TEMPLATES = new HashMap<>();

    static {
        TASK_TEMPLATES.put("FAST_REPLY", "Reply to a LinkedIn message briefly.");
        TASK_TEMPLATES.put("QUALITY_REPLY", "Write a thoughtful LinkedIn message.");
        TASK_TEMPLATES.put("REFERRAL", "Request a job referral from a connection.");
        TASK_TEMPLATES.put("COLD_PITCH", "Write a cold outreach to a recruiter.");
        TASK_TEMPLATES.put("CONNECTION", "Write a LinkedIn connection request.");
        TASK_TEMPLATES.put("EMAIL", "Write a professional email reply.");
    }

    public static String buildPrompt(String action, String tone, String context,
                                     String recipientName, String targetRole, String targetCompany) {
        action = normalizeAction(action);
        tone = sanitize(tone, "Professional");
        context = sanitizeContext(context);
        recipientName = sanitize(recipientName, "");
        targetRole = sanitize(targetRole, "");
        targetCompany = sanitize(targetCompany, "");

        String task = TASK_TEMPLATES.getOrDefault(action, "Write a professional message.");

        StringBuilder prompt = new StringBuilder();
        prompt.append("TASK: ").append(task).append("\n");
        prompt.append("TONE: ").append(tone).append("\n");

        if (!recipientName.isEmpty()) {
            prompt.append("TO: ").append(recipientName).append("\n");
        }
        if (!targetRole.isEmpty()) {
            prompt.append("ROLE: ").append(targetRole).append("\n");
        }
        if (!targetCompany.isEmpty()) {
            prompt.append("COMPANY: ").append(targetCompany).append("\n");
        }

        prompt.append("\nINPUT:\n").append(context).append("\n");
        prompt.append("\nOUTPUT: Reply only with the message. No labels, no extra text.");

        return prompt.toString();
    }

    public static String buildFastPrompt(String tone, String context) {
        tone = sanitize(tone, "Professional");
        context = sanitizeContext(context);

        return "TASK: Reply to LinkedIn message.\n" +
               "TONE: " + tone + "\n" +
               "MAX: 2 sentences.\n" +
               "INPUT:\n" + context + "\n" +
               "OUTPUT: Message only.";
    }

    private static String normalizeAction(String action) {
        if (action == null || action.isBlank()) {
            return "QUALITY_REPLY";
        }
        return action.toUpperCase().replace(" ", "_");
    }

    private static String sanitizeContext(String context) {
        if (context == null || context.isBlank()) {
            return "No context provided.";
        }
        context = context.trim();
        if (context.length() > 500) {
            context = context.substring(0, 500);
        }
        return context;
    }

    private static String sanitize(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}
