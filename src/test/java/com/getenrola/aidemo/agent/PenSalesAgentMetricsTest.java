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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PenSalesAgentMetricsTest {

    @Autowired
    private PenSalesOpenAiAgent penSalesOpenAiAgent;

    @Autowired
    private PenSalesCustomEvaluator evaluator;

    private AgentReply call(String user) {
        AgentReply reply = penSalesOpenAiAgent.execute(new AgentRequest(user, null, null));
        System.out.printf("%nUser: %s%nAgent: %s [stage=%s, interest=%s]%n",
                user, reply.text(), reply.salesStage(), reply.leadInterest());
        return reply;
    }

    private EvaluationResult eval(String user, AgentReply reply) {
        EvaluationResult result = evaluator.evaluate(user, reply.text());
        System.out.printf("EVAL -> on_topic=%d, persona=%d, task=%d, pass=%s, reason=%s%n",
                result.on_topic_score,
                result.persona_score,
                result.task_appropriateness,
                result.final_pass,
                result.explanation);
        return result;
    }

    @Test
    @DisplayName("Full Agent Metrics Evaluation Across Multiple Conversations")
    void metricsEvaluationSuite() {

        List<EvaluationResult> allResults = new ArrayList<>();

        // ---------- Conversation Set 1 ----------
        String[] convo1 = {
                "Hi, my name is Fred.",
                "How much is the pen?",
                "Seems expensive!",
                "Can you tell me a joke about cats?",
                "Hi, I'm looking for a pen for signing contracts."
        };

        AgentReply last = null;
        for (String user : convo1) {
            last = call(user);
            allResults.add(eval(user, last));
        }

        // ---------- Conversation Set 2 ----------
        String[] convo2 = {
                "Hello!",
                "I need something smooth for long writing sessions.",
                "Do you have something more comfortable?",
                "Is there a premium option?",
                "Okay, how do I purchase it?"
        };

        for (String user : convo2) {
            last = call(user);
            allResults.add(eval(user, last));
        }

        // ---------- Conversation Set 3 ----------
        String[] convo3 = {
                "What's the best pen you have?",
                "Does it smear?",
                "Is it good for left-handed writers?",
                "Cool, I think I want it.",
                "Send me the link."
        };

        for (String user : convo3) {
            last = call(user);
            allResults.add(eval(user, last));
        }

        // --------- METRICS CALCULATION ----------
        double avgOnTopic = allResults.stream()
                .mapToInt(r -> r.on_topic_score)
                .average().orElse(0);

        double avgPersona = allResults.stream()
                .mapToInt(r -> r.persona_score)
                .average().orElse(0);

        double taskRate = allResults.stream()
                .mapToInt(r -> r.task_appropriateness)
                .average().orElse(0);

        long passCount = allResults.stream()
                .filter(r -> r.final_pass)
                .count();

        double passRate = (double) passCount / allResults.size();

        // --------- PRINT SUMMARY ----------
        System.out.println("\n================== AGENT METRICS SUMMARY ==================");
        System.out.printf("Total turns evaluated: %d%n", allResults.size());
        System.out.printf("Average On-Topic Score: %.2f / 3%n", avgOnTopic);
        System.out.printf("Average Persona Score: %.2f / 3%n", avgPersona);
        System.out.printf("Task Appropriateness Rate: %.2f%n", taskRate);
        System.out.printf("Final Pass Rate: %.2f%n", passRate);
        System.out.println("===========================================================\n");

        // --------- ASSERTIONS (optional strictness) ----------
        assertThat(avgOnTopic).isGreaterThanOrEqualTo(2.0);
        assertThat(avgPersona).isGreaterThanOrEqualTo(2.0);
        assertThat(passRate).isGreaterThan(0.80); // at least 80% of turns should pass
    }
}
