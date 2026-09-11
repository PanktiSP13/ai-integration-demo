package com.pinu.ai_integration_demo_project.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinu.ai_integration_demo_project.data.model.Chat
import com.pinu.ai_integration_demo_project.data.model.Message
import com.pinu.ai_integration_demo_project.data.model.SenderType
import com.pinu.ai_integration_demo_project.data.repository.AIRepository
import com.pinu.ai_integration_demo_project.data.repository.ChatRepository
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

    fun createChat(name: String, role: String) {
        repository.createChat(name, role)
    }


    //v1
    fun askAI(chatId:String ,prompt: String) {
        viewModelScope.launch {
            repository.createMessage(chatId, prompt, SenderType.USER)
            _isTyping.value = true
            val response = aiRepository.askAI(prompt)
            _isTyping.value = false
            repository.createMessage(chatId, response, SenderType.AI)
        }
    }

    //v2
    fun askAIStream(chatId: String, prompt: String) {
        viewModelScope.launch {
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
    fun askAIStreamWithChatSession(chatId: String, prompt: String) {
        viewModelScope.launch {

            // create user message
            repository.createMessage(chatId = chatId, content = prompt, senderType = SenderType.USER)

            // create AI message
            val aiMessage = repository.createMessage(chatId = chatId, content = "", senderType = SenderType.AI)

            // show typing indicator until we get response from AI
            _isTyping.value = true

            try {

                aiRepository.askAIStream(chatId = chatId, prompt = prompt).collect { chunk ->
                    _isTyping.value = false
                    repository.updateMessage(messageId = aiMessage.messageId, chunk = chunk, chatId = chatId)
                }

            } catch (e: Exception) {

                _isTyping.value = false
                repository.updateMessage(messageId = aiMessage.messageId, chunk = "Something went wrong: ${e.message}",
                    senderType = SenderType.AI, chatId = chatId)
            }
        }


    }



    fun getChatById(chatId: String): Chat? {
        return repository.getChatById(chatId)
    }
}
