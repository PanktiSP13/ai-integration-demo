package com.pinu.ai_integration_demo_project.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pinu.ai_integration_demo_project.data.model.Chat
import com.pinu.ai_integration_demo_project.data.model.Message
import com.pinu.ai_integration_demo_project.data.model.SenderType
import com.pinu.ai_integration_demo_project.data.model.bank_support.TransactionAnalysis
import com.pinu.ai_integration_demo_project.data.repository.AIRepository
import com.pinu.ai_integration_demo_project.data.repository.chat_support.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository,
    private val aiRepository: AIRepository) : ViewModel() {

    val chats: StateFlow<List<Chat>> = repository.chats.stateIn(
        viewModelScope, SharingStarted.Lazily, emptyList()
    )

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    fun getMessages(chatId: String): StateFlow<List<Message>> {
        return repository.getMessages(chatId).stateIn(
            viewModelScope, SharingStarted.Lazily, emptyList()
        )
    }

    fun createChat(name: String, role: String, id: String? = null) {
        viewModelScope.launch {
            if (id != null) {
                repository.createChat(name, role, id)
            } else {
                repository.createChat(name, role)
            }
        }
    }

    suspend fun ensureChatExists(chatId: String, name: String, role: String) {
        if (repository.getChatById(chatId) == null) {
            repository.createChat(name, role, chatId)
        }
    }


    //v1
    fun askAI(chatId: String, role: String, prompt: String) {
        viewModelScope.launch {
            if (repository.getChatById(chatId) == null) {
                repository.createChat(name = role, role = role, id = chatId)
            }
            repository.createMessage(chatId, prompt, SenderType.USER)
            _isTyping.value = true
            val response = aiRepository.askAI(prompt)
            _isTyping.value = false
            repository.createMessage(chatId, response, SenderType.AI)
        }
    }

    //v2
    fun askAIStream(chatId: String, role: String, prompt: String) {
        viewModelScope.launch {
            if (repository.getChatById(chatId) == null) {
                repository.createChat(name = role, role = role, id = chatId)
            }
            repository.createMessage(chatId, prompt, SenderType.USER)
            _isTyping.value = true

            var response = ""
            aiRepository.askAIStream(prompt).collect { chunk ->
                if (response.isEmpty()) {
                    response += chunk
                    _isTyping.value = false
                    repository.createMessage(chatId, response, SenderType.AI)
                } else {
                    repository.updateMessage(chatId, getMessages(chatId).value.last().messageId, chunk, SenderType.AI)
                }
            }
        }
    }


    //v3
    fun askAIStreamWithChatSession(chatId: String, role: String, prompt: String) {
        viewModelScope.launch {
            // Ensure chat exists to satisfy foreign key constraint
            if (repository.getChatById(chatId) == null) {
                repository.createChat(name = role, role = role, id = chatId)
            }

            // create user message
            repository.createMessage(chatId = chatId, content = prompt, senderType = SenderType.USER)

            // show typing indicator until we get response from AI
            _isTyping.value = true

            var aiMessageId: String? = null

            try {
                aiRepository.askAIStream(chatId = chatId, role = role, prompt = prompt).collect { chunk ->
                    val currentId = aiMessageId
                    if (currentId == null) {
                        // first chunk received, hide typing indicator and create AI message
                        _isTyping.value = false
                        val aiMessage = repository.createMessage(chatId = chatId, content = chunk, senderType = SenderType.AI)
                        aiMessageId = aiMessage.messageId
                    } else {
                        repository.updateMessageV2(messageId = currentId, chunk = chunk, chatId = chatId)
                    }
                }
            } catch (e: Exception) {
                _isTyping.value = false
                val errorMsg = "Something went wrong: ${e.message}"
                val currentId = aiMessageId
                if (currentId == null) {
                    repository.createMessage(chatId = chatId, content = errorMsg, senderType = SenderType.AI)
                } else {
                    repository.updateMessageV2(messageId = currentId, chunk = errorMsg, chatId = chatId)
                }
            }
        }
    }

    fun askBankSupportAI(chatId: String, role: String, prompt: String) {
        viewModelScope.launch {

            if (repository.getChatById(chatId) == null) {
                repository.createChat(name = role, role = role, id = chatId)
            }
            repository.createMessage(chatId, prompt, SenderType.USER)

            _isTyping.value = true
            val response = aiRepository.askBankSupportAI(chatId,role,prompt)
            _isTyping.value = false

            val result = Gson().fromJson(response, TransactionAnalysis::class.java)
            repository.createMessage(chatId, result.toString(), SenderType.AI)
        }
    }

    fun askBankAppSupportAI(chatId: String, role: String, prompt: String) {
        viewModelScope.launch {

            if (repository.getChatById(chatId) == null) {
                repository.createChat(name = role, role = role, id = chatId)
            }
            repository.createMessage(chatId, prompt, SenderType.USER)

            _isTyping.value = true
            val response = aiRepository.askBankAppSupportAI(chatId,role,prompt)
            _isTyping.value = false

            repository.createMessage(chatId, response, SenderType.AI)
        }
    }

    suspend fun getChatById(chatId: String): Chat? {
        return repository.getChatById(chatId)
    }
}
