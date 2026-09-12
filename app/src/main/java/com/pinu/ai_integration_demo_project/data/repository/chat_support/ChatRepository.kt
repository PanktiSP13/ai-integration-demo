package com.pinu.ai_integration_demo_project.data.repository.chat_support

import com.pinu.ai_integration_demo_project.data.local.chat_support.dao.ChatDao
import com.pinu.ai_integration_demo_project.data.local.chat_support.dao.MessageDao
import com.pinu.ai_integration_demo_project.data.local.chat_support.entities.ChatEntity
import com.pinu.ai_integration_demo_project.data.local.chat_support.entities.MessageEntity
import com.pinu.ai_integration_demo_project.data.model.Chat
import com.pinu.ai_integration_demo_project.data.model.Message
import com.pinu.ai_integration_demo_project.data.model.SenderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao
) {
    val chats: Flow<List<Chat>> = chatDao.getAllChats().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getMessages(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getMessagesSync(chatId: String): List<Message> {
        return messageDao.getMessagesForChatSync(chatId).map { it.toDomain() }
    }

    suspend fun createChat(name: String, role: String, id: String = UUID.randomUUID().toString()): Chat {
        val chat = Chat(id = id, name = name, role = role)
        chatDao.insertChat(chat.toEntity())
        return chat
    }

    suspend fun getChatById(chatId: String): Chat? {
        return chatDao.getChatById(chatId)?.toDomain()
    }

    suspend fun createMessage(chatId: String, content: String, senderType: SenderType): Message {
        val message = Message(chatId = chatId, content = content, senderType = senderType)
        messageDao.insertMessage(message.toEntity())
        return message
    }

    suspend fun updateMessage(chatId: String, messageId: String, chunk: String, senderType: SenderType = SenderType.AI) {
        val existingMessages = messageDao.getMessagesForChatSync(chatId)
        val targetMessage = existingMessages.find { it.messageId == messageId && it.senderType == senderType }
        targetMessage?.let {
            val updatedContent = it.content + chunk
            messageDao.updateMessageContent(messageId, updatedContent)
        }
    }

    // New version of updateMessage used in askAIStreamWithChatSession
    suspend fun updateMessageV2(messageId: String, chunk: String, chatId: String) {
        val existingMessages = messageDao.getMessagesForChatSync(chatId)
        val targetMessage = existingMessages.find { it.messageId == messageId }
        targetMessage?.let {
            val updatedContent = it.content + chunk
            messageDao.updateMessageContent(messageId, updatedContent)
        }
    }

    // Helper extensions for mapping
    private fun ChatEntity.toDomain() = Chat(id = id, name = name, role = role)
    private fun Chat.toEntity() = ChatEntity(id = id, name = name, role = role)
    private fun MessageEntity.toDomain() = Message(
        messageId = messageId,
        chatId = chatId,
        content = content,
        senderType = senderType,
        timestamp = timestamp
    )
    private fun Message.toEntity() = MessageEntity(
        messageId = messageId,
        chatId = chatId,
        content = content,
        senderType = senderType,
        timestamp = timestamp
    )
}