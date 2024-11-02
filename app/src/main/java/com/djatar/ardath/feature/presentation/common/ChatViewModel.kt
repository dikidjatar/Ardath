package com.djatar.ardath.feature.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.ChatState
import com.djatar.ardath.feature.domain.models.MessageState
import com.djatar.ardath.feature.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
)  : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private var lastVisibleChatKey: String? = null

    fun loadChats(batchSize: Int = 10) {
        if (_chatState.value.isLoading) return
        _chatState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.listenForChats(batchSize, lastVisibleChatKey)
                .distinctUntilChanged()
                .collectLatest { resource ->
                _chatState.update {
                    when (resource) {
                        is Resource.Error -> it.copy(isLoading = false, error = resource.message)
                        is Resource.Success -> it.copy(
                            chats = resource.data ?: emptyList(),
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
                if (!resource.data.isNullOrEmpty()) {
                    lastVisibleChatKey = resource.data?.lastOrNull()?.id
                }
            }
        }
    }

    fun setSelectedChatId(chatId: String?) {
        var value: String? = chatId
        if (value == null) {
            value = repository.getChatId()
        }
        _chatState.value.selectedChatId = value
    }

    fun hasChat(otherUserId: String, callback: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.hasChat(otherUserId).collectLatest {
                withContext(Dispatchers.Main) { callback(it) }
            }
        }
    }

    fun sendMessage(
        otherUserId: String,
        chatId: String,
        chatTitle: String,
        messageText: String?,
        imageUrl: String? = null
    ) = repository.sendMessage(
        otherUserId,
        chatId,
        chatTitle,
        messageText,
        imageUrl
    )

    fun listenForMessages(chatId: String) {
        if (_chatState.value.messages.isLoading) return
        _chatState.value.apply {
            messages = messages.copy(isLoading = true, error = "")
        }

        viewModelScope.launch {
            repository.listenForMessages(chatId).collectLatest { resource ->
                _chatState.update {
                    when (resource) {
                        is Resource.Error -> it.copy(messages = MessageState(
                            isLoading = false,
                            messages = resource.data ?: emptyList()),
                            error = resource.message
                        )
                        is Resource.Success -> {
                            it.copy(messages = MessageState(
                                isLoading = false,
                                messages = resource.data ?: emptyList())
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}