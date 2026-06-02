package com.linkup.ai.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
@Service
public class LinkedInAIService {

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

        String context = request.getEmailContent();
if (context == null) context = "";
context = context.trim();

if (context.length() > 1200) {
    context = context.substring(0, 1200);
}
        String tone = (request.getTone() == null || request.getTone().isBlank())
                ? "Professional"
                : request.getTone();

        String action = (request.getAction() == null || request.getAction().isBlank())
                ? "LinkedIn Reply"
                : request.getAction();

        boolean fastMode = isFastMode(action);

        String prompt = fastMode
                ? buildFastPrompt(context, tone)
                : buildLinkedInPrompt(request, context, tone, action);
if (prompt == null || prompt.trim().isEmpty()) {
    prompt = "Write a professional LinkedIn reply.";
}
        Map<String, Object> body = Map.of(
        "model", "llama-3.3-70b-versatile",
        "messages", List.of(
                Map.of(
                        "role", "user",
                        "content", prompt
                )
        ),
        "temperature", fastMode ? 0.4 : 0.7,
        "max_tokens", fastMode ? 150 : 400
);
System.out.println("===== GROQ REQUEST BODY =====");
System.out.println(body);
System.out.println("===== PROMPT =====");
System.out.println(prompt);

        try {
            String response = webClient.post()
                    .uri(groqApiUrl)
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return new LinkedInResponse(extractResponse(response), tone, action);

        } catch (Exception e) {
            return new LinkedInResponse("Error: " + e.getMessage(), tone, action);
        }
    }

    private String extractResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

        } catch (Exception e) {
            return "Failed to parse response.";
        }
    }

    private boolean isFastMode(String action) {
        return "LinkedIn Reply".equalsIgnoreCase(action);
    }

    private String buildFastPrompt(String context, String tone) {
        return """
You are a LinkedIn assistant that writes fast replies.

RULES:
- Max 2 sentences
- Tone: %s
- Natural human style

MESSAGE:
%s

Return only reply text.
""".formatted(tone, context);
    }

  private String buildLinkedInPrompt(LinkedInRequest request, String context, String tone, String action) {

    String instructions = "";

    switch (action) {

        case "LinkedIn Follow-up":
            instructions = """
You are writing a LinkedIn referral request.

IMPORTANT:
- The sender is the job applicant.
- The recipient is an employee working at the target company.
- The sender wants a referral for THEMSELVES.
- Do NOT recommend the job to the recipient.
- Do NOT assume the recipient is applying.
- Politely ask for a referral.
- Mention the target role and company naturally.
- Keep it professional and concise.
""";
            break;

        case "Cold Pitching Note":
            instructions = """
You are writing a cold outreach message to a recruiter.

IMPORTANT:
- The sender is interested in a role at the company.
- Briefly mention relevant skills and interest.
- Keep it short and professional.
- Do not sound desperate.
""";
            break;

        case "Connection Request":
            instructions = """
You are writing a LinkedIn connection request.

IMPORTANT:
- Maximum 300 characters.
- Friendly and professional.
- Do not immediately ask for a referral.
- Focus on establishing a connection.
""";
            break;

        default:
            instructions = """
You are writing a professional LinkedIn message.
Keep it natural and conversational.
""";
    }

    return """
%s

TONE:
%s

RECIPIENT:
%s

TARGET ROLE:
%s

TARGET COMPANY:
%s

ADDITIONAL CONTEXT:
%s

Return only the final message.
""".formatted(
            instructions,
            tone,
            safe(request.getRecipientName()),
            safe(request.getTargetRole()),
            safe(request.getTargetCompany()),
            context
    );
}

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }
}