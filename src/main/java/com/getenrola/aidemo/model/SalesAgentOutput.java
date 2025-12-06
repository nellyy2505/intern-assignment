package com.getenrola.aidemo.model;

// Structured output for each agent turn.

public record SalesAgentOutput(
        String replyText,
        String salesStage,
        String leadInterest
) {}
