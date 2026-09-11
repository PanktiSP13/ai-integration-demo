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

    fun createMessage(chatId: String, content: String, senderType: SenderType) : Message {
        val message = Message(chatId = chatId, content = content, senderType = senderType)
        _messages.value += message
        return  message
    }

    fun updateMessage(chatId: String,messageId:String, chunk: String, senderType: SenderType = SenderType.AI) {
        _messages.value = _messages.value.map {
            if (it.chatId == chatId && it.senderType == senderType && it.messageId == messageId) {
                it.copy(content = it.content + chunk)
            } else {
                it
            }
        }
    }
}
