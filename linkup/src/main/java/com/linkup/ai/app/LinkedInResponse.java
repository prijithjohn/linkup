package com.linkup.ai.app;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkedInResponse {
    private String reply;
    private String tone;
    private String action;
}