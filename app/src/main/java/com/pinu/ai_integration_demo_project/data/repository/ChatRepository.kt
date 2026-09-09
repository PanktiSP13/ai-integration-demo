package com.pinu.ai_integration_demo_project.data.repository

import com.pinu.ai_integration_demo_project.data.model.Chat
import com.pinu.ai_integration_demo_project.data.model.Message
import com.pinu.ai_integration_demo_project.data.model.SenderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ChatRepository {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: Flow<List<Chat>> = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())

    fun getMessages(chatId: String): Flow<List<Message>> {
        return _messages.map { it.filter { msg -> msg.chatId == chatId } }
    }

    fun createChat(name: String, role: String): Chat {
        val newChat = Chat(name = name, role = role)
        _chats.value += newChat
        return newChat
    }

    fun getChatById(chatId: String): Chat? {
        return _chats.value.find { it.id == chatId }
    }

    suspend fun createMessage(chatId: String, content: String, senderType: SenderType) {
      val message = Message(chatId = chatId, content = content, senderType = senderType)
        _messages.value += message

//        val userMessage = Message(chatId = chatId, content = content, senderType = SenderType.USER)
//        _messages.value += userMessage
//
//        // Mock AI response
//        delay(1000.milliseconds)
//        val chat = getChatById(chatId)
//        val aiContent = "Hello! As a ${chat?.role ?: "Assistant"}, I'm here to help. You said: $content"
//        val aiMessage = Message(chatId = chatId, content = aiContent, senderType = SenderType.AI)
//        _messages.value += aiMessage
    }
}
