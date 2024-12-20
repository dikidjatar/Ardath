package com.djatar.ardath.feature.presentation.common

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Chat
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

    private val _messageState = MutableStateFlow(MessageState())
    val messageState = _messageState.asStateFlow()

    val multiSelectState = mutableStateOf(false)
    val selectedChatState = mutableStateListOf<Chat>()

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

    fun toggleSelection(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = _chatState.value.chats[index]
            val selectedChat = selectedChatState.find { it.id == item.id }
            if (selectedChat != null) {
                selectedChatState.remove(selectedChat)
            } else {
                selectedChatState.add(item)
            }
            multiSelectState.value = selectedChatState.isNotEmpty()
        }
    }

    fun clearSelection() {
        multiSelectState.value = false
        selectedChatState.clear()
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
        if (_messageState.value.isLoading) return
        _messageState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.listenForMessages(chatId)
                .distinctUntilChanged()
                .collectLatest { resource ->
                _messageState.update {
                    when (resource) {
                        is Resource.Error ->
                            it.copy(isLoading = false, error = resource.message)
                        is Resource.Success -> {
                            it.copyWithMessages(resource.data ?: emptyList())
                        }
                    }
                }
            }
        }
    }

    fun deleteChat(chats: List<Chat>, onFinish: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteChat(chats, onFinish)
        }
    }

    fun clear() {
        lastVisibleChatKey = null
        _chatState.value = ChatState()
        clearSelection()
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}