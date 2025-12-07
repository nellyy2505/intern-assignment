package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PenSalesOpenAiAgentTest {

    @Autowired
    private PenSalesOpenAiAgent penSalesOpenAiAgent;

    @Test
    void testSalesFlowWithMemoryAndStructuredOutput() {

        // Turn 1
        String user1 = "Hi, my name is Fred";
        System.out.println("\nUser: " + user1);

        AgentReply reply1 = penSalesOpenAiAgent.execute(
                new AgentRequest(user1, null, null)
        );

        System.out.println("Agent: " + reply1.text());
        assertThat(reply1.text()).isNotBlank();
        assertThat(reply1.salesStage()).isNotBlank();
        assertThat(reply1.leadInterest()).isNotBlank();

        // Turn 2
        String user2 = "How much is the pen?";
        System.out.println("\nUser: " + user2);

        AgentReply reply2 = penSalesOpenAiAgent.execute(
                new AgentRequest(user2, null, null)
        );

        System.out.println("Agent: " + reply2.text());
        assertThat(reply2.text()).isNotBlank();
        assertThat(reply2.salesStage()).isNotBlank();
        assertThat(reply2.leadInterest()).isNotBlank();

        // Turn 3
        String user3 = "Seems expensive!";
        System.out.println("\nUser: " + user3);

        AgentReply reply3 = penSalesOpenAiAgent.execute(
                new AgentRequest(user3, null, null)
        );

        System.out.println("Agent: " + reply3.text());
        assertThat(reply3.text()).isNotBlank();
        assertThat(reply3.salesStage()).isNotBlank();
        assertThat(reply3.leadInterest()).isNotBlank();

        // Turn 4
        String user4 = "Can you email me a brochure?";
        System.out.println("\nUser: " + user4);

        AgentReply reply4 = penSalesOpenAiAgent.execute(
                new AgentRequest(user4, null, null)
        );

        System.out.println("Agent: " + reply4.text());
        assertThat(reply4.text()).isNotBlank();
        assertThat(reply4.salesStage()).isNotBlank();
        assertThat(reply4.leadInterest()).isNotBlank();
    }
}
