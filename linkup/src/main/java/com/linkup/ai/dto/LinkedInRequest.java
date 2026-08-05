package com.linkup.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for AI reply generation.
 * Supports multiple use cases: LinkedIn fast-reply and quality reply.
 */
@Data
public class LinkedInRequest {

    @JsonAlias({"emailContent", "message"})
    @Size(max = 4000)
    private String messageContent;

    @Size(max = 100)
    private String tone;

    @Size(max = 100)
    private String action;

    @Size(max = 100)
    private String recipientName;

    @Size(max = 200)
    private String targetRole;

    @Size(max = 200)
    private String targetCompany;

    public void setMessage(String message) {
        this.messageContent = message;
    }
}
