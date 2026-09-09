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



    fun askAI(chatId:String ,prompt: String) {
        viewModelScope.launch {
            repository.createMessage(chatId, prompt, SenderType.USER)
            _isTyping.value = true
            val response = aiRepository.askAI(prompt)
            _isTyping.value = false
            repository.createMessage(chatId, response, SenderType.AI)
        }
    }

    fun getChatById(chatId: String): Chat? {
        return repository.getChatById(chatId)
    }
}
