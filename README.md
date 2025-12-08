# 🖋️ **Pen Sales AI Agent — LLM-Orchestrated Sales Workflow**

## 🚀 Overview

This project implements an **LLM-powered sales agent** designed to sell pens using a structured sales flow, behavioral constraints, and a fully automated evaluation pipeline.
The goal is not just to “chat,” but to produce **consistent, sales-appropriate, state-machine-valid responses** in a multi-turn conversation.

---

# ✅ **What I Built**

## 1. **Prompt Engineering: Meta-Prompting + Few-Shot + RAG-Fusion**

To stabilize the agent’s behaviour, I used a layered prompting strategy:

### **Meta-Prompting**

A high-level instruction block that defines:

* the sales persona
* behavioural constraints
* the funnel stages
* strict rules for redirection, objection handling, pricing, and closing

This ensures the model acts like a **sales agent**, not a casual chatbot.

### **Few-Shot Examples**

Added curated examples for:

* discovery questions
* objection handling
* off-topic redirection
* closing behaviour

Few-shot grounding significantly reduces drift and teaches the model consistent patterns.

### **RAG-Fusion** (Retriever-Augmented Generation Fusion)

Although product knowledge is small (pens), the RAG layer:

* injects domain facts (price, features, refillability, premium options)
* stabilizes answer quality
* ensures factual consistency

RAG-Fusion also helps keep responses *on brand* and reduces hallucination risk.

Combined, these three techniques dramatically improved:

* persona consistency
* sales-flow appropriateness
* redirection behaviour
* factual correctness

---

## 2. **Structured Output (JSON Format)**

The agent returns structured JSON on every turn, containing:

```json
{
  "text": "...",
  "salesStage": "DISCOVERY | PRESENTATION | TEMPERATURE_CHECK | COMMITMENT | ACTION",
  "leadInterest": "COLD | CURIOUS | INTERESTED | UNSURE | READY_TO_BUY"
}
```

This design:

* enables **state-machine validation**
* allows downstream automation to interpret intent
* makes evaluation deterministic
* prevents the LLM from drifting into unstructured chit-chat

---

## 3. **Refactor to Spring AI (Aligned with Spring AI Documentation)**

The application was rebuilt using **Spring AI** patterns and abstractions:

* `ChatClient` for prompt building
* declarative system/user messages
* JSON deserialization helpers
* lower temperature + safety prompts

I followed the official Spring AI recommended architecture, including:

* builder patterns
* LLM-inference separation
* environment-based configuration

This refactor simplifies reasoning, ensures future extensibility, and keeps the agent **aligned with modern Spring AI best practices**.

---

## 4. **Evaluation Pipeline (LLM-as-a-Judge)**

The default Spring RelevancyEvaluator was insufficient (semantic similarity only).
Therefore I implemented **PenSalesCustomEvaluator**, a task-aware black-box evaluation module.

### **The Evaluator Scores Each Turn on:**

* **On-topic score (0–3):**
  Whether the agent stays anchored to pen sales even during off-topic user input.

* **Persona score (0–3):**
  Whether the agent behaves like a professional sales assistant (not therapist, friend, etc.)

* **Task appropriateness (0/1):**
  Whether the reply matches the expected behaviour for the user intent type:

  * greeting → discovery
  * pricing → provide value/price
  * objection → value framing
  * off-topic → redirection
  * buying signal → move toward closing

* **Final pass/fail decision**

### **Why LLM-as-a-Judge?**

Because conventional unit tests cannot evaluate:

* tone
* persona consistency
* conversation flow
* redirection accuracy
* value framing

Using an LLM judge allows evaluation of **intent-level correctness**, not just string comparison.

### **Test Suite Design**

Two test layers:

#### **A. Behaviour Tests (`PenSalesOpenAiAgentTest`)**

Per-turn strict validation:

* structured JSON output
* correct funnel stage
* evaluator pass/fail

#### **B. Metrics Test (`PenSalesAgentMetricsTest`)**

Runs multiple conversations and computes:

* avg. on-topic score
* avg. persona score
* task-appropriateness rate
* overall pass rate


# 🧠 Conversation Memory

The agent uses **Spring AI’s `ChatMemory`** to keep track of previous turns so it can follow a coherent sales flow. Memory is enabled through the `MessageChatMemoryAdvisor`, which is added during agent construction:

```java
MessageChatMemoryAdvisor.builder(chatMemory).build()
```

All turns share a fixed conversation ID:

```java
param(ChatMemory.CONVERSATION_ID, "console-pen-sales")
```

This tells Spring AI to store and replay the full conversation history automatically.
The agent never manually appends old messages—Spring AI injects them into every model call.

**Why it matters:**
Memory allows the agent to remember what the user said earlier, maintain the correct sales stage, track interest level, and respond consistently instead of starting fresh each turn.


# ▶️ **How to Run**

### **1. Requirements**

* Java 17+
* Maven 3+
* OpenAI or compatible API key (Ollama, LM Studio, Claude, etc.)

### **2. Set your API key**

```bash
export OPENAI_API_KEY=your-key
```


### **3. Run the application**

```bash
./mvnw spring-boot:run 
```

### **4. Run tests**

Only behaviour tests:

```bash
./mvnw -Dtest=PenSalesOpenAiAgentTest test
```

Metrics / evaluation suite:

```bash
./mvnw -Dtest=PenSalesAgentMetricsTest test
```

---

# 📁 **Project Structure (Relevant Sections)**

```
src/
  main/
    java/com/getenrola/aidemo/
      agent/
        PenSalesOpenAiAgent.java
      evaluation/
        PenSalesCustomEvaluator.java
        EvaluationResult.java

  test/
    java/com/getenrola/aidemo/agent/
      PenSalesOpenAiAgentTest.java
      PenSalesAgentMetricsTest.java
```

---

# 📝 Notes

* Evaluation is **task-aware**, not semantic-similarity-based.
* The agent uses **structured prompting**, **few-shot**, **meta prompting**, and **RAG-fusion**.
* All tests use an **LLM-as-a-judge**, but with a deterministic temperature setting.
* This architecture can generalize to any product sales agent or guided workflow assistant.


