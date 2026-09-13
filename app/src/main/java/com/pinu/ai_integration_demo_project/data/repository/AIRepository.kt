package com.pinu.ai_integration_demo_project.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.pinu.ai_integration_demo_project.data.model.SenderType
import com.pinu.ai_integration_demo_project.data.repository.chat_support.ChatRepository
import com.pinu.ai_integration_demo_project.data.schemas.transactionSchema
import com.pinu.ai_integration_demo_project.data.system_instructions.bankingRoleInstructions
import com.pinu.ai_integration_demo_project.data.system_instructions.defaultRoleInstructions
import com.pinu.ai_integration_demo_project.data.tool_executors.BankingToolExecutor
import com.pinu.ai_integration_demo_project.data.tool_executors.ToolCalls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AIRepository(
    private val chatRepository: ChatRepository,
    private val bankingToolExecutor: BankingToolExecutor,
) {

    private val globalModel = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(modelName = "gemini-3.5-flash-lite")

    private val chatSessions = mutableMapOf<String, Chat>() // Chat from firebase


    private fun createModel(role: String, generationConfig: GenerationConfig? = null,tools : List<Tool>?= null): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.5-flash-lite",
            systemInstruction = createSystemInstruction(role),
            generationConfig = generationConfig,
            tools = tools
        )
    }

    private fun createSystemInstruction(role: String): Content {
        return content { text(if (role == "Banking Application Support") bankingRoleInstructions else defaultRoleInstructions(role)) }
    }

    // v1 -> non-streaming response from AI model
    suspend fun askAI(prompt: String): String {
        val response = globalModel.generateContent(prompt).text.orEmpty()
        Log.e("Pankti", "askAI: $response")
        return response
    }

    // v2 -> streaming response from AI model
    suspend fun askAIStream(prompt: String): Flow<String> = flow {
        try {
            globalModel.generateContentStream(prompt).collect { chunk ->
                emit(chunk.text.orEmpty())
                Log.e("Pankti", "askAIStream: ${chunk.text.orEmpty()}")
            }
        } catch (e: Exception) {
            Log.e("Pankti", "askAIStream: ${e.message.orEmpty()}")
            if (e.message?.contains("You exceeded your current quota") == true) {
                emit("Limit exceeded for today. Please try again tomorrow.")
            }
        }
    }

    //v3 -> maintaining chat session to provide chat context to AI model for better response
    private suspend fun getOrCreateChatSession(chatId: String, role: String,generationConfig: GenerationConfig? = null,tools : List<Tool>?= null): Chat {

        // 1. Return existing session
        val session = chatSessions[chatId]
        if (session != null) return session


        // 2. Create Gemini model with this chat's role
        val model = createModel(role, generationConfig, tools)


        // 3. Restore history from DB
        val history = chatRepository.getMessagesSync(chatId).map { message ->
            content(role = if (message.senderType == SenderType.USER) "user" else "model") {
                text(message.content)
            }
        }

        val newSession = model.startChat(history = history)
        chatSessions[chatId] = newSession
        return newSession
    }

    fun askAIStream(chatId: String, role: String, prompt: String): Flow<String> = flow {
        try {
            val chat = getOrCreateChatSession(chatId, role)

            chat.sendMessageStream(prompt)
                .collect { chunk ->
                    val text = chunk.text.orEmpty()

                    if (text.isNotEmpty()) {
                        emit(text)
                    }

                    Log.e("AI_STREAM","chunk=$text")

                }

        } catch (e: Exception) {

            Log.e("AI_STREAM", "chatId=$chatId error=${e.message}", e)

            if (e.message?.contains("You exceeded your current quota") == true) {
                emit("Limit exceeded for today. Please try again tomorrow.")
            } else {
                throw e
            }
        }
    }

    //v4 -> structured output from AI model
    suspend fun askBankSupportAI(chatId:String,role: String,prompt: String): String {
        var generationConfig: GenerationConfig? = null
        if (chatId == "banking_support") {
            generationConfig = generationConfig {
                responseMimeType = "application/json" // Return only JSON. Don't explain anything. Use these exact fields. Don't add Markdown. Don't add extra text.
                responseSchema = transactionSchema
            }
        }
        val model = createModel(role, generationConfig)

        val response = model.generateContent(prompt).text.orEmpty()
        Log.e("Pankti", "askAI: $response")
        return response
    }


    //v5 -> function calling from AI model (Tool calls)
    // Talk to Gemini → detect tool calls → ask ToolExecutor to execute them → send result back → return final text.
    suspend fun askBankAppSupportAI(chatId:String,role: String,prompt: String): String{
        try {
            val chat = getOrCreateChatSession(chatId, role,tools = listOf(ToolCalls.bankingTool))
            var response = chat.sendMessage(prompt)
            Log.e("AI_STREAM", "askBankAppSupportAI: ${response.text.toString()}")

            var toolCallCount = 0
            val maxToolCalls = 5

            while (response.functionCalls.isNotEmpty()) {

                if (++toolCallCount > maxToolCalls) {
                    return "I couldn't complete the request."
                }
                Log.e("AI_FUNCTION", "Function calls detected: ${response.functionCalls.size}")

                val functionResponseParts = response.functionCalls.map { functionCall ->
                    Log.e("AI_FUNCTION", "Executing: ${functionCall.name}")
                    Log.e("AI_FUNCTION", "Arguments: ${functionCall.args.toList().joinToString(", ")}")
                    bankingToolExecutor.execute(functionCall)
                }


                // Send tool results back to Gemini
                response = chat.sendMessage(
                    content("user") {
                        functionResponseParts.forEach { functionResponse ->
                            part(functionResponse)
                        }
                    }
                )

                Log.e("AI_FUNCTION", "Response after tool execution: ${response.text.toString()}")

            }

            // No more function calls.
            // This is the final Gemini response.
            Log.e("AI_FUNCTION", "Final response: ${response.text.orEmpty()}")
            return response.text.orEmpty()

        } catch (e: Exception) {

            Log.e("AI_STREAM", "chatId=$chatId error=${e.message}", e)

            return if (e.message?.contains("You exceeded your current quota") == true) {
                "Limit exceeded for today. Please try again tomorrow."
            } else {
                "Something went wrong"
            }
        }
    }

}
