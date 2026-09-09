package com.pinu.ai_integration_demo_project.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

class AIRepository {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel("gemini-3.7-flash")

    // v1
    suspend fun askAI(prompt: String): String {
        val response =  model.generateContent(prompt).text.orEmpty()
        Log.e("Pankti", "askAI: $response")
        return response
    }

    // v2
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
}