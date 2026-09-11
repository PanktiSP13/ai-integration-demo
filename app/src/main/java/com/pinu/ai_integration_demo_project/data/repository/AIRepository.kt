package com.pinu.ai_integration_demo_project.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.pinu.ai_integration_demo_project.data.model.SenderType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

class AIRepository(private val chatRepository: ChatRepository) {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel("gemini-3.5-flash-lite")

    private val chatSessions = mutableMapOf<String, Chat>() // Chat from firebase


    // v1 -> non-streaming response from AI model
    suspend fun askAI(prompt: String): String {
        val response =  model.generateContent(prompt).text.orEmpty()
        Log.e("Pankti", "askAI: $response")
        return response
    }

    // v2 -> streaming response from AI model
    suspend fun askAIStream(prompt: String): Flow<String> = flow {
        try {
            model.generateContentStream(prompt).collect { chunk ->
                emit(chunk.text.orEmpty())
                Log.e("Pankti", "askAIStream: ${chunk.text.orEmpty()}")
                delay(100.milliseconds)
            }
        } catch (e: Exception) {
            Log.e("Pankti", "askAIStream: ${e.message.orEmpty()}")
            if (e.message?.contains("You exceeded your current quota") == true) {
                emit("Limit exceeded for today. Please try again tomorrow.")
            }
        }
    }

    //v3 -> maintaining chat session to provide chat context to AI model for better response
    private suspend fun getOrCreateChatSession(chatId: String): Chat {
        val session = chatSessions[chatId]
        if (session != null) return session

        // Restore history from DB
        val history = chatRepository.getMessagesSync(chatId).map { message ->
            content(role = if (message.senderType == SenderType.USER) "user" else "model") {
                text(message.content)
            }
        }

        val newSession = model.startChat(history = history)
        chatSessions[chatId] = newSession
        return newSession
    }

    fun askAIStream(chatId: String, prompt: String): Flow<String> = flow {
        try {
            val chat = getOrCreateChatSession(chatId)

            chat.sendMessageStream(prompt)
                .collect { chunk ->

                    val text = chunk.text.orEmpty()

                    if (text.isNotEmpty()) {
                        emit(text)
                    }

                    Log.e("AI_STREAM","chunk=$text")

                    delay(100.milliseconds)
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

}
