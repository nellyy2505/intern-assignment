package com.getenrola.aidemo.ui;

import com.getenrola.aidemo.agent.PenSalesOpenAiAgent;
import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleChat implements CommandLineRunner {

    private final PenSalesOpenAiAgent penSalesOpenAiAgent;

    public ConsoleChat(PenSalesOpenAiAgent penSalesOpenAiAgent) {
        this.penSalesOpenAiAgent = penSalesOpenAiAgent;
    }

    @Override
    public void run(String... args) {
        System.out.println("Pen Sales Agent (type 'exit' to quit)\n");

        List<OpenAiApi.ChatCompletionMessage> history = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                if (!scanner.hasNextLine()) break;       // EOF (Ctrl+D/Ctrl+Z)
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;

                AgentReply reply = penSalesOpenAiAgent.execute(
                        new AgentRequest(line, null, null)
                );
                System.out.println("Agent: " + reply.text());
                System.out.println("  [stage=" + reply.salesStage() + ", interest=" + reply.leadInterest() + "]\n");

                // append user + assistant messages to history for next turn
                history.add(new OpenAiApi.ChatCompletionMessage(
                        line,
                        OpenAiApi.ChatCompletionMessage.Role.USER
                ));

                history.add(new OpenAiApi.ChatCompletionMessage(
                        reply.text(),
                        OpenAiApi.ChatCompletionMessage.Role.ASSISTANT
                ));

            }
        }

        System.out.println("Goodbye 👋");
    }
}
