package com.pinu.ai_integration_demo_project.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class AIRepository {

    suspend fun askAI(prompt: String): String {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()
        ).generativeModel("gemini-3.7-flash")
        val response =  model.generateContent(prompt).text.orEmpty()
        Log.e("Pankti", "askAI: $response")
        return response
    }
}