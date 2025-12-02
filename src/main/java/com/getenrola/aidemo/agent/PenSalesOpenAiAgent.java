package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class PenSalesOpenAiAgent {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            You are a sales agent who must sell a very fancy, one-of-a-kind pen.
            The pen costs $5000. It has black ink. It has a titanium case encrusted with diamonds.
            You are communicating with a customer via SMS.
            """;

    public PenSalesOpenAiAgent(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public AgentReply execute(AgentRequest agentRequest) {

        String reply = chatClient
                .prompt()
                .user(agentRequest.userText())
                .call()        // returns ChatResponse
                .content();    // returns a String

        return new AgentReply(reply, null);
    }

}