package com.getenrola.aidemo.model;

public record AgentReply(
        String text,
        String responseId,
        String salesStage,
        String leadInterest
) {}
