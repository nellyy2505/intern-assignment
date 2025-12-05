package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentReply;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PenSalesOpenAiAgentTest {

    @Autowired
    private PenSalesOpenAiAgent penSalesOpenAiAgent;

    @Test
    void testSalesFlowWithHistory() {

        List<OpenAiApi.ChatCompletionMessage> history = new ArrayList<>();

        String user1 = "Hi, my name is Fred";
        System.out.println("User: " + user1);

        AgentReply reply1 = penSalesOpenAiAgent.execute(history, user1);
        System.out.println("Agent: " + reply1.text());
        assertThat(reply1.text()).isNotBlank();

        history.add(new OpenAiApi.ChatCompletionMessage(user1, OpenAiApi.ChatCompletionMessage.Role.USER));
        history.add(new OpenAiApi.ChatCompletionMessage(reply1.text(), OpenAiApi.ChatCompletionMessage.Role.ASSISTANT));

        String user2 = "How much is the pen?";
        System.out.println("User: " + user2);

        AgentReply reply2 = penSalesOpenAiAgent.execute(history, user2);
        System.out.println("Agent: " + reply2.text());
        assertThat(reply2.text()).isNotBlank();

        history.add(new OpenAiApi.ChatCompletionMessage(user2, OpenAiApi.ChatCompletionMessage.Role.USER));
        history.add(new OpenAiApi.ChatCompletionMessage(reply2.text(), OpenAiApi.ChatCompletionMessage.Role.ASSISTANT));

        String user3 = "Seems expensive!";
        System.out.println("User: " + user3);

        AgentReply reply3 = penSalesOpenAiAgent.execute(history, user3);
        System.out.println("Agent: " + reply3.text());
        assertThat(reply3.text()).isNotBlank();

        history.add(new OpenAiApi.ChatCompletionMessage(user3, OpenAiApi.ChatCompletionMessage.Role.USER));
        history.add(new OpenAiApi.ChatCompletionMessage(reply3.text(), OpenAiApi.ChatCompletionMessage.Role.ASSISTANT));

        String user4 = "Can you email me a brochure?";
        System.out.println("User: " + user4);

        AgentReply reply4 = penSalesOpenAiAgent.execute(history, user4);
        System.out.println("Agent: " + reply4.text());
        assertThat(reply4.text()).isNotBlank();
    }
}
