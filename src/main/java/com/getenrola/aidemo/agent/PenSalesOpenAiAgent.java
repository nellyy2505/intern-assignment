package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentReply;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PenSalesOpenAiAgent {

    private final OpenAiApi openAiApi;

    private static final String MODEL_NAME = "gpt-4o-mini"; // or any chat completion model

    private static final String SYSTEM_PROMPT = """
            You are a sales agent who must sell a very fancy, one-of-a-kind pen.
            The pen costs $5000. It has black ink. It has a titanium case encrusted with diamonds.
            You are communicating with a customer via SMS.
            """;

    public PenSalesOpenAiAgent(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    /**
     * @param history  previous chat messages (user + assistant), as ChatCompletionMessages
     * @param userText current user input
     */
    public AgentReply execute(List<OpenAiApi.ChatCompletionMessage> history, String userText) {

        List<OpenAiApi.ChatCompletionMessage> messages = new ArrayList<>();

        // System message first
        messages.add(new OpenAiApi.ChatCompletionMessage(
                SYSTEM_PROMPT,
                OpenAiApi.ChatCompletionMessage.Role.SYSTEM
        ));

        // Previous turns
        messages.addAll(history);

        // Current user message
        messages.add(new OpenAiApi.ChatCompletionMessage(
                userText,
                OpenAiApi.ChatCompletionMessage.Role.USER
        ));

        // Build ChatCompletionRequest (messages + model + temperature)
        OpenAiApi.ChatCompletionRequest request =
                new OpenAiApi.ChatCompletionRequest(messages, MODEL_NAME, 0.6);

        var responseEntity = openAiApi.chatCompletionEntity(request);
        var completion = responseEntity.getBody();

        if (completion == null || completion.choices().isEmpty()) {
            return new AgentReply("Sorry, I couldn't think of a response just now.", null);
        }

        var firstChoice = completion.choices().get(0);
        var assistantMessage = firstChoice.message();

        // response content is always a String according to ChatCompletionMessage docs
        String replyText = (String) assistantMessage.rawContent();

        return new AgentReply(replyText, completion.id());
    }
}
