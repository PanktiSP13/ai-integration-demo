package com.pinu.ai_integration_demo_project.data.model

import java.util.UUID

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String
)

data class Message(
    val messageId: String = UUID.randomUUID().toString(),
    val chatId: String,
    val content: String,
    val senderType: SenderType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SenderType {
    USER, AI
}
