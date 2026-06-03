package com.linkup.ai.app;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response payload for all AI reply endpoints.
 * Guaranteed immutable contract: {reply, tone, action}
 * Never null - fallback values always provided if AI response fails.
 */
@Data
@AllArgsConstructor
public class LinkedInResponse {
    private String reply;
    private String tone;
    private String action;
}