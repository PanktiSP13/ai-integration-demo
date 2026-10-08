# 🤖 Android AI Integration Demo Project

> An end-to-end native Android application demonstrating progressive Generative AI integration capabilities using **Firebase AI (Google Gemini 3.5 Flash Lite)**, **Jetpack Compose**, **Room Database**, **Function Calling / Tool Execution**, **Autonomous AI Agent Loops**, and **Retrieval-Augmented Generation (RAG)** architecture.

---





## 📑 Table of Contents
- [📖 Overview & Brief Summary](#-overview--brief-summary)
- [✨ Key Features & Functionality](#-key-features--functionality)
- [🛠️ Tech Stack](#️-tech-stack)
- [🚀 AI Capabilities & Evolutionary Stages](#-ai-capabilities--evolutionary-stages)
  - [V1: Basic Direct Prompting](#v1-basic-direct-prompting-askai)
  - [V2: Token-by-Token Streaming](#v2-token-by-token-streaming-askaistream)
  - [V3: Multi-Turn Conversational Chat Session](#v3-multi-turn-conversational-chat-session-askaistreamwithchatsession)
  - [V4: Structured JSON Schema Output](#v4-structured-json-schema-output-askbanksupportai)
  - [V5: Function Calling / Tool Execution](#v5-function-calling--tool-execution-askbankappsupportai)
  - [V6: Autonomous AI Agent Loop](#v6-autonomous-ai-agent-loop-askbanksupportaiagent)
  - [Bonus: HRMS RAG Architecture Blueprint](#bonus-retrieval-augmented-generation-rag-blueprint)
- [🔄 Application Flow](#-application-flow)
- [🛡️ How We Handle (Error Handling, Edge Cases & Guardrails)](#️-how-we-handle-error-handling-edge-cases--guardrails)
- [📂 Project Structure](#-project-structure)
- [🏁 Getting Started](#-getting-started)

---

## 📖 Overview & Brief Summary

The **AI Integration Demo Project** is a production-grade demonstration of how modern mobile applications can harness Large Language Models (LLMs) natively in Android. Built with idiomatic Kotlin, Jetpack Compose, and clean MVVM architecture, it showcases an evolutionary progression of AI patterns—moving from simple prompt-response interactions to intelligent tool execution and multi-step autonomous AI agents.

The app simulates a dual-purpose enterprise solution:
1. **Personal AI Assistants Platform**: Users can create custom AI assistants with defined roles (e.g., Tutor, Tech Lead, Chef) with persistent chat history.
2. **Banking Support & Autonomous AI Agent**: A banking system powered by local Room DB and Gemini function calling that allows the AI to look up transaction statuses, query account balances, and fetch recent account activity dynamically.
3. **HRMS Policy RAG Blueprint**: An architectural blueprint demonstrating semantic search, embedding stores, policy chunking, and grounded prompt engineering for internal enterprise policies.

---

## ✨ Key Features & Functionality

- 💬 **Custom Assistant Creation**: Dynamically create role-based AI assistants (e.g., Tutor, Chef, Support Agent) with tailored persona system instructions.
- ⚡ **Real-Time Token Streaming**: Low-latency typing effect using Kotlin Coroutine `Flow` for real-time text chunk rendering.
- 📜 **Native Markdown Rendering**: AI responses are rendered using native Jetpack Compose Markdown (formatting bold text, lists, code blocks, tables).
- 💾 **Local Chat History Persistence**: Complete chat history and message states cached offline in SQLite via Room DB.
- 🏦 **Banking Support AI**:
  - **Structured Analysis**: Returns typed JSON schemas for classification (payment category, issue type, priority, escalation needs).
  - **Function Calling**: Automatically invokes native Kotlin functions to query database records (e.g., "What is my balance for ACC001?").
  - **Autonomous AI Agent**: Evaluates requests in a loop, deciding when and which tools to call until enough context is gathered to answer the user.
- 📊 **Automatic Data Bootstrapping**: Automatically seeds mock accounts and transaction data from JSON assets into Room DB on application start.

---

## 🛠️ Tech Stack

| Domain | Technology / Library | Description |
| :--- | :--- | :--- |
| **Language** | [Kotlin 2.2.10](https://kotlinlang.org/) | Modern, expressive, asynchronous Kotlin with Coroutines & Flow |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative Compose UI with Material Design 3 components |
| **Architecture** | MVVM + Repository Pattern | Clean separation of concerns with unidirectional data flow |
| **AI SDK** | [Firebase AI Client SDK](https://firebase.google.com/docs/ai) | Google AI Backend (`gemini-3.5-flash-lite`) |
| **Local Database** | [Room DB 2.8.5](https://developer.android.com/training/data-storage/room) | SQLite abstraction layer compiled with KSP |
| **Navigation** | [Navigation Compose 2.10.0](https://developer.android.com/jetpack/compose/navigation) | Type-safe declarative screen routing |
| **Markdown** | [Compose Markdown 1.0.4](https://github.com/boswelja/compose-markdown) | Native Material 3 Compose Markdown renderer |
| **Serialization** | Kotlinx Serialization & Gson | JSON schema construction and model parsing |
| **Security** | Firebase App Check | Debug provider factory initialization for developer safety |

---

## 🚀 AI Capabilities & Evolutionary Stages

The core AI engine in [`AIRepository.kt`](app/src/main/java/com/pinu/ai_integration_demo_project/data/repository/AIRepository.kt) demonstrates 6 distinct stages of AI maturity:

```
[V1: Direct Prompt] ➔ [V2: Streaming] ➔ [V3: Chat Session Memory] ➔ [V4: Structured JSON] ➔ [V5: Function Calling] ➔ [V6: Autonomous AI Agent]
```

---

### V1: Basic Direct Prompting (`askAI`)
- **Concept**: Synchronous request-response call.
- **Method**: `globalModel.generateContent(prompt)`
- **Use Case**: Simple single-turn generation tasks without context or typing animation.

---

### V2: Token-by-Token Streaming (`askAIStream`)
- **Concept**: Emits text chunks as they are generated by the Gemini model.
- **Method**: `globalModel.generateContentStream(prompt)` returning Kotlin `Flow<String>`.
- **Use Case**: Reduces perceived latency and improves UX with instant feedback.

---

### V3: Multi-Turn Conversational Chat Session (`askAIStreamWithChatSession`)
- **Concept**: Restores historical conversation messages from Room DB into a Gemini `Chat` session.
- **Method**: `model.startChat(history = history)` and `chat.sendMessageStream(prompt)`.
- **Use Case**: Enables multi-turn conversations where the model remembers prior context (e.g., "What was the previous order ID we discussed?").

---

### V4: Structured JSON Schema Output (`askBankSupportAI`)
- **Concept**: Forces the model to respond strictly in valid JSON matching a defined schema.
- **Implementation**:
  ```kotlin
  val generationConfig = generationConfig {
      responseMimeType = "application/json"
      responseSchema = transactionSchema
  }
  ```
- **Schema (`transactionSchema`)**:
  - `category`: `UPI_PAYMENT`, `CARD_PAYMENT`, `BANK_TRANSFER`, `OTHER`
  - `issue`: `FAILED`, `DUPLICATE`, `REFUND_PENDING`, `UNKNOWN`
  - `priority`: `LOW`, `MEDIUM`, `HIGH`
  - `requires_human_support`: `Boolean`
  - `amount`: `Integer`
- **Use Case**: Structured issue categorization, sentiment analysis, and ticket triaging.

---

### V5: Function Calling / Tool Execution (`askBankAppSupportAI`)
- **Concept**: Exposes native Android application capabilities to the AI via function declarations.
- **Tool Declarations** ([`BankingToolDefinitions.kt`](app/src/main/java/com/pinu/ai_integration_demo_project/data/tool_executors/BankingToolDefinitions.kt)):
  1. `getTransactionStatus(transactionId)`: Fetches transaction details by ID.
  2. `getAccountBalance(accountId)`: Queries account type, balance, and currency.
  3. `getRecentTransactions(accountId, limit)`: Retrieves recent account transactions.
- **Execution Flow**:
  1. User asks: *"What is the status of transaction TXN002?"*
  2. Gemini returns a `FunctionCallPart` for `getTransactionStatus(transactionId="TXN002")`.
  3. [`BankingToolExecutor`](app/src/main/java/com/pinu/ai_integration_demo_project/data/tool_executors/BankingToolExecutor.kt) queries Room DB and returns a `FunctionResponsePart`.
  4. Result is sent back to Gemini; Gemini formats a human-friendly final response: *"Your UPI transaction TXN002 for ₹2,500 at Merchant XYZ failed."*

---

### V6: Autonomous AI Agent Loop (`askBankSupportAIAgent`)
- **Concept**: A reasoning engine loop where the AI autonomously decides which tools to invoke, processes execution results, and determines if additional information is required before answering.
- **Agent Loop Implementation**:
  ```kotlin
  var toolCallCount = 0
  val maxToolCalls = 5

  while (response.functionCalls.isNotEmpty()) {
      if (++toolCallCount > maxToolCalls) {
          return "Max tool call limit reached."
      }
      val functionResponseParts = response.functionCalls.map { functionCall ->
          bankingToolExecutor.execute(functionCall)
      }
      response = chat.sendMessage(content("user") { functionResponseParts.forEach { part(it) } })
  }
  ```
- **Use Case**: Complex multi-step queries (e.g., *"Check my balance on ACC001, and if it's over ₹50,000 show my last 3 transactions"*).

---

### Bonus: Retrieval-Augmented Generation (RAG) Blueprint
an architecture for HRMS Policy QA:
- **`PolicyChunk`**: Semi-structured document chunks (Policy name, section, version, text).
- **`EmbeddedPolicyChunk`**: Vector representation paired with policy chunk.
- **`PolicyVectorStore`**: In-memory vector database with cosine similarity search.
- **`PolicyRetrievalService`**: Interfacing embedding models (Gemini / Vertex AI) to retrieve top-K relevant chunks.
- **`PolicyContextBuilder`**: Grounding prompt constructor to eliminate model hallucinations.

---

## 🔄 Application Flow

```
                     ┌──────────────────────────┐
                     │     DashboardScreen      │
                     └────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          ▼                       ▼                       ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ Banking Support  │    │ Bank App Support │    │ Bank Support AI  │
│ (Structured JSON)│    │ (Function Call)  │    │  (Agent Loop)    │
└────────┬─────────┘    └────────┬─────────┘    └────────┬─────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                  │
                                  ▼
                        ┌──────────────────┐
                        │    ChatScreen    │
                        └────────┬─────────┘
                                  │
                                  ▼
                        ┌──────────────────┐
                        │  ChatViewModel   │
                        └────────┬─────────┘
                                  │
                                  ▼
                        ┌──────────────────┐
                        │   AIRepository   │
                        └────────┬─────────┘
                                  │
                  ┌───────────────┴───────────────┐
                  ▼                               ▼
       ┌────────────────────┐          ┌─────────────────────┐
       │ Firebase Gemini AI │          │ BankingToolExecutor │
       └────────────────────┘          └──────────┬──────────┘
                                                  │
                                                  ▼
                                       ┌─────────────────────┐
                                       │       Room DB       │
                                       └─────────────────────┘
```

1. **Launch & Data Seeding**: `MainActivity` initializes Room DB and launches `BankingMockDataLoader` to seed `accounts.json` and `transactions.json` into local SQLite tables.
2. **Dashboard Navigation**: Navigates into specific AI chat modes or accesses custom user assistants.
3. **Message Input**: User submits a message in `ChatScreen`. `ChatViewModel` creates a local `USER` message in Room DB.
4. **AI Generation**: `AIRepository` communicates with Firebase AI (Gemini). For function calls, `BankingToolExecutor` retrieves live database rows and returns results back to Gemini.
5. **UI Update**: `ChatViewModel` receives chunks or final responses, updates Room DB, and streams output directly to the Compose UI.

---

## 🛡️ How We Handle (Error Handling, Edge Cases & Guardrails)

### 1. Quota & Rate Limit Interception
- Detects Gemini API quota exhaustion errors (`"You exceeded your current quota"`).
- Gracefully emits user-friendly system messages: *"Limit exceeded for today. Please try again tomorrow."*

### 2. Autonomous Agent Infinite Loop Prevention
- Caps execution loop at `maxToolCalls = 5`.
- Aborts and alerts the user if the model enters a repetitive function calling loop.

### 3. Tool Function Argument Safeguards
- Validates parameters inside `BankingToolExecutor` (e.g., missing transaction ID or null account ID).
- Coerces parameters to safe bounds (e.g., limits transaction fetch counts using `.coerceIn(1, 10)`).

### 4. Persona & Role Boundaries
- Utilizes strict system instructions ([`RoleBasedSystemInstructions.kt`](app/src/main/java/com/pinu/ai_integration_demo_project/data/system_instructions/RoleBasedSystemInstructions.kt)).
- Unrelated user queries receive a standard refusal response: *"I can only help with topics related to my role."*

### 5. Data Privacy & Formatting Rules
- Explicit rules instruct Gemini to mask raw function call JSON, internal details, and excessive fields from the end user.

### 6. Relational Database Integrity
- Room DB configured with `PRAGMA foreign_keys = ON;`.
- `ensureChatExists()` checks and creates chat headers before creating message records to satisfy foreign key constraints.

---

## 📂 Project Structure

```text
com.pinu.ai_integration_demo_project
├── MyApplication.kt                     # Application class initializing Firebase App Check
├── MainActivity.kt                      # Activity entry point & Mock Data Bootstrapping
├── data
│   ├── local
│   │   ├── AppDatabase.kt               # Room database setup
│   │   ├── bank_support                 # DAOs and Entities for Accounts & Transactions
│   │   └── chat_support                 # DAOs and Entities for Chats & Messages
│   ├── model
│   │   ├── Models.kt                    # Chat & Message domain models
│   │   ├── bank_support                 # Transaction Analysis data structures
│   │   └── hrms_support                 # RAG / Vector store blueprint models
│   ├── repository
│   │   ├── AIRepository.kt              # Core Gemini AI engine (V1 - V6 implementations)
│   │   ├── bank_support                 # Account & Transaction database repositories
│   │   └── chat_support                 # Chat persistence repository
│   ├── schemas
│   │   └── BankingAppSupportSchema.kt   # Gemini JSON response schemas
│   ├── system_instructions
│   │   └── RoleBasedSystemInstructions.kt # System prompts for role personas & agents
│   └── tool_executors
│       ├── BankingToolDefinitions.kt    # Function declarations for Gemini Tools
│       └── BankingToolExecutor.kt       # Executor mapping tool calls to Room DB queries
└── ui
    ├── ChatViewModel.kt                 # StateFlow & UI business logic
    ├── chat                             # Chat screen & native Markdown message bubbles
    ├── chatlist                         # Saved chats list screen
    ├── create                           # Custom assistant dialog
    ├── dashboard                        # Main feature dashboard
    ├── navigation                       # Navigation graph configuration
    ├── theme                            # Material Design 3 theme configuration
    └── utils                            # Mock data loader from JSON assets
```

---

## 🏁 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or higher
- JDK 11+
- Android Device or Emulator running API 28 (Android 9.0) or higher
- Firebase Project configured with Firebase AI / Gemini API enabled

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/AI_Integration_Demo_Project.git
   cd AI_Integration_Demo_Project
   ```

2. **Add Firebase Configuration**:
   - Create a project on the [Firebase Console](https://console.firebase.google.com/).
   - Enable **Firebase AI / Vertex AI for Firebase** in your project settings.
   - Download `google-services.json` and place it in the `app/` directory.

3. **Build and Run**:
   - Open the project in Android Studio.
   - Sync Gradle project.
   - Select an emulator or physical device and click **Run (Shift + F10)**.

---

<p align="center">Made with ❤️ for Android & AI Developers</p>

<img alt="Screenshot_20261008_212039" src="https://github.com/user-attachments/assets/9456b949-60dd-4b54-8b0d-f0e60d694088" width="150"/>
<img alt="Screenshot_20261008_213912" src="https://github.com/user-attachments/assets/3a674fa8-2cfb-44d1-97c4-02629c25a7e5" width="150"/>
<img alt="Screenshot_20260912_151648" src="https://github.com/user-attachments/assets/58c2a5b8-ffce-4c23-90bb-5d99929f14b4" width="150" />
<img  alt="Screenshot_20260915_213806" src="https://github.com/user-attachments/assets/e542ae77-4c86-4353-8586-15b4bf2917fd" width="150"/>
<img alt="Screenshot_20260911_134026" src="https://github.com/user-attachments/assets/c1b9a871-9626-4daa-9327-e0f3ae45c189" width="150" />
<img  alt="Screenshot_20260911_192408" src="https://github.com/user-attachments/assets/9c73fac8-5cc7-4313-acdb-3680bcda3a92" width="150"/>
<img alt="Screenshot_20260911_220040" src="https://github.com/user-attachments/assets/760636a1-486f-45a4-92ce-ed49c8f3f84d" width="150"/>



