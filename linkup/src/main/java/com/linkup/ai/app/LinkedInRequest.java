package com.linkup.ai.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LinkedInRequest {

    @NotBlank(message = "Email content cannot be empty")
    private String emailContent;

    private String tone;
    private String action;

    // 🧠 NEW: Explicit AI routing control
    // FAST = extension
    // PRO = web app
    private String mode;

    private String recipientName;
    private String targetRole;
    private String targetCompany;
}