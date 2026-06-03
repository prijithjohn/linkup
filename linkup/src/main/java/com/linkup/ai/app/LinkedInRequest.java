package com.linkup.ai.app;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LinkedInRequest {

    @JsonAlias({"emailContent", "message"})
    private String messageContent;

    private String tone;
    private String action;
    private String recipientName;
    private String targetRole;
    private String targetCompany;

    public void setMessage(String message) {
        this.messageContent = message;
    }
}
