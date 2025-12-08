package com.getenrola.aidemo.evaluation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class PenSalesCustomEvaluator {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
        You are an expert evaluator for a pen-sales AI assistant.

        Your job is to judge whether the assistant's reply is appropriate for the sales task,
        not whether it matches the literal meaning of the user’s message.

        The assistant must ALWAYS behave as a focused pen-sales assistant:
        - keep discussion anchored to pens or buying a pen
        - gather needs
        - present product value
        - handle objections
        - redirect off-topic requests back to pen context
        - maintain a sales persona
        - guide toward a purchase when appropriate

        Ignore semantic similarity. This is NOT QA matching.
        Evaluate ONLY task-appropriateness and sales-behaviour correctness.

        SCORING:
        - on_topic_score (0–3)
        - persona_score (0–3)
        - task_appropriateness (0 or 1)
        - final_pass (boolean)
            final_pass = true ONLY IF:
                on_topic_score >= 2
                AND persona_score >= 2
                AND task_appropriateness == 1

        Return ONLY JSON:
        {
          "on_topic_score": <int>,
          "persona_score": <int>,
          "task_appropriateness": <int>,
          "final_pass": <boolean>,
          "explanation": "<string>"
        }
        """;

    public PenSalesCustomEvaluator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public EvaluationResult evaluate(String userMessage, String agentReply) {

        String evalPrompt = """
            Evaluate the assistant’s reply.

            User message:
            %s

            Assistant reply:
            %s

            Return JSON only.
            """.formatted(userMessage, agentReply);

        if(evalPrompt==null) throw new IllegalArgumentException("evalPrompt cannot be null");

        String json = this.chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(evalPrompt)
                .call()
                .content();

        return EvaluationResult.fromJson(json);
    }
}
