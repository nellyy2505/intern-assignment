package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PenSalesOpenAiAgentTest {

    @Autowired
    private PenSalesOpenAiAgent penSalesOpenAiAgent;

    @Test
    void testScript() {
        var agentRequest = new AgentRequest("Hi, my name is Fred", null, null);
        System.out.println("User: " + agentRequest.userText());
        var agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.text()).isNotNull();

        agentRequest = new AgentRequest("How much is the pen?", null, null);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.text()).isNotNull();

        agentRequest = new AgentRequest("Seems expensive!", null, null);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.text()).isNotNull();

        agentRequest = new AgentRequest("Can you email me a brochure?", null, null);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.text()).isNotNull();

    }

}
