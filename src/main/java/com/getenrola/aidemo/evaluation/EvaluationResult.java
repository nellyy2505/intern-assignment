package com.getenrola.aidemo.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EvaluationResult {

    public int on_topic_score;
    public int persona_score;
    public int task_appropriateness;
    public boolean final_pass;
    public String explanation;

    public static EvaluationResult fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, EvaluationResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse evaluator JSON: " + json, e);
        }
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "on_topic_score=" + on_topic_score +
                ", persona_score=" + persona_score +
                ", task_appropriateness=" + task_appropriateness +
                ", final_pass=" + final_pass +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
