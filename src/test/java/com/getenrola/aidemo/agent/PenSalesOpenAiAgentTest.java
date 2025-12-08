package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.evaluation.EvaluationResult;
import com.getenrola.aidemo.evaluation.PenSalesCustomEvaluator;
import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PenSalesOpenAiAgentTest {

    @Autowired
    private PenSalesOpenAiAgent penSalesOpenAiAgent;

    @Autowired
    private PenSalesCustomEvaluator customEvaluator;

    private static final Set<String> ALLOWED_STAGES = Set.of(
            "DISCOVERY",
            "PRESENTATION",
            "TEMPERATURE_CHECK",
            "COMMITMENT",
            "ACTION"
    );

    private static final Set<String> ALLOWED_INTEREST = Set.of(
            "COLD",
            "CURIOUS",
            "INTERESTED",
            "UNSURE",
            "READY_TO_BUY"
    );

    private AgentReply callAgent(String userText) {
        AgentReply reply = penSalesOpenAiAgent.execute(
                new AgentRequest(userText, null, null)
        );
        System.out.printf("%nUser:  %s%nAgent: %s [stage=%s, interest=%s]%n",
                userText, reply.text(), reply.salesStage(), reply.leadInterest());
        return reply;
    }

    private EvaluationResult evaluateCustom(String userText, AgentReply reply) {
        return customEvaluator.evaluate(userText, reply.text());
    }

    private void assertStructuredOutput(AgentReply reply) {
        assertThat(reply.text())
                .as("replyText should not be empty")
                .isNotBlank();

        assertThat(reply.salesStage())
                .as("salesStage must be one of the allowed enum values")
                .isIn(ALLOWED_STAGES);

        assertThat(reply.leadInterest())
                .as("leadInterest must be one of the allowed enum values")
                .isIn(ALLOWED_INTEREST);
    }

    // ---------- tests ----------

    @Test
    @DisplayName("Single-turn: discovery question is relevant and structurally valid")
    void discoveryTurn_relevantAndStructured() {
        String user = "Hi, I’m looking for a pen for signing contracts.";
        AgentReply reply = callAgent(user);

        // 1) basic structured-output contract
        assertStructuredOutput(reply);

        // 2) content-level evaluation using custom LLM-as-a-judge
        EvaluationResult eval = evaluateCustom(user, reply);
        assertThat(eval.final_pass)
                .as("Agent reply should be appropriate for the pen-sales task: %s", eval)
                .isTrue();
    }

    @Test
    @DisplayName("Multi-turn: price question + objection stay on-topic and structured")
    void multiTurn_salesFlowWithRelevancyChecks() {
        // Turn 1 – greeting / light discovery
        String u1 = "Hi, my name is Fred.";
        AgentReply turn1 = callAgent(u1);
        assertStructuredOutput(turn1);
        assertThat(turn1.salesStage())
                .as("First turn should be at DISCOVERY or PRESENTATION at most")
                .isIn("DISCOVERY", "PRESENTATION");

        EvaluationResult eval1 = evaluateCustom(u1, turn1);
        assertThat(eval1.final_pass)
                .as("Greeting reply should still behave like a pen-sales assistant: %s", eval1)
                .isTrue();

        // Turn 2 – pricing question
        String q2 = "How much is the pen?";
        AgentReply turn2 = callAgent(q2);
        assertStructuredOutput(turn2);

        EvaluationResult eval2 = evaluateCustom(q2, turn2);
        assertThat(eval2.final_pass)
                .as("Pricing answer should be appropriate for pen pricing (task-aware): %s", eval2)
                .isTrue();

        // Turn 3 – price objection
        String q3 = "Seems expensive!";
        AgentReply turn3 = callAgent(q3);
        assertStructuredOutput(turn3);

        assertThat(turn3.salesStage())
                .as("Objection should typically be handled around TEMPERATURE_CHECK or COMMITMENT")
                .isIn("TEMPERATURE_CHECK", "COMMITMENT", "PRESENTATION");

        EvaluationResult eval3 = evaluateCustom(q3, turn3);
        assertThat(eval3.final_pass)
                .as("Objection handling reply should talk about value/benefits of the pen: %s", eval3)
                .isTrue();
    }

    @Test
    @DisplayName("Off-topic request: agent should stay pen-focused and still pass task-aware eval")
    void offTopic_nudgedBackToPenContext() {
        String user = "Can you tell me a joke about cats?";
        AgentReply reply = callAgent(user);
        assertStructuredOutput(reply);

        // Even if user goes off-topic, agent should gently steer back to pens.
        EvaluationResult eval = evaluateCustom(user, reply);
        assertThat(eval.final_pass)
                .as("Agent should avoid pure small talk and connect response back to the pen product: %s", eval)
                .isTrue();
    }
}
