package com.linkup.ai.app;

public class AIConstants {

    // Action types
    public static final String ACTION_FAST_REPLY = "FAST_REPLY";
    public static final String ACTION_QUALITY_REPLY = "QUALITY_REPLY";
    public static final String ACTION_REFERRAL = "REFERRAL";
    public static final String ACTION_COLD_PITCH = "COLD_PITCH";
    public static final String ACTION_CONNECTION = "CONNECTION";

    // Tone defaults
    public static final String DEFAULT_TONE = "Professional";

    // Groq API configuration
    public static final String GROQ_MODEL = "llama-3.3-70b-versatile";
    public static final int FAST_MODE_MAX_TOKENS = 150;
    public static final int QUALITY_MODE_MAX_TOKENS = 400;
    public static final double FAST_MODE_TEMPERATURE = 0.4;
    public static final double QUALITY_MODE_TEMPERATURE = 0.7;

    // Context limits
    public static final int MAX_CONTEXT_LENGTH = 500;

    // Fallback messages
    public static final String FALLBACK_REPLY = "Unable to generate a response at this time. Please try again.";
    public static final String DEFAULT_ACTION = "QUALITY_REPLY";

    // Error handling
    public static final String ERROR_NULL_RESPONSE = "Invalid AI response received.";

    // JSON response paths
    public static final String RESPONSE_PATH_CHOICES = "choices";
    public static final String RESPONSE_PATH_MESSAGE = "message";
    public static final String RESPONSE_PATH_CONTENT = "content";
}
