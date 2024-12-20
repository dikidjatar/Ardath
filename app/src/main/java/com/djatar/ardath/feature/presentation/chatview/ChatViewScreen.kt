package com.djatar.ardath.feature.presentation.chatview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.domain.models.MessageItem
import com.djatar.ardath.feature.domain.models.MessageState
import com.djatar.ardath.feature.presentation.chatview.components.EmptyMessage
import com.djatar.ardath.feature.presentation.chatview.components.MessageInputContainer
import com.djatar.ardath.feature.presentation.chatview.components.MessageItemContent
import com.djatar.ardath.feature.presentation.chatview.components.MessageItemHeader
import com.djatar.ardath.feature.presentation.common.ChatViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "ChatScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatViewScreen(
    chatViewModel: ChatViewModel,
    messageState: StateFlow<MessageState>,
    title: String = "",
    paddingValues: PaddingValues,
    onSendMessage: (chatTitle: String, messageText: String) -> Unit,
    onNavigateToProfile: (route: String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val state by messageState.collectAsStateWithLifecycle()

    val messageText = rememberSaveable { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state) {
        lazyListState.apply {
            if (state.messages.isNotEmpty()) {
                scrollToItem(state.messages.lastIndex)
            }
        }
    }

    BackHandler { onNavigateBack() }

    val bottomPadding = if (!WindowInsets.isImeVisible) 10.dp else 0.dp
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(
            bottom = paddingValues.calculateBottomPadding() + bottomPadding
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (!state.isLoading && state.messages.isEmpty()) {
                EmptyMessage()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding() + 60.dp)
                        .imePadding(),
                    state = lazyListState
                ) {
                    when {
                        state.isLoading -> item {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), gapSize = 100.dp)
                        }
                        !state.error.isNullOrEmpty() -> item {
                            Text(
                                text = stringResource(R.string.fetching_message_error),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {
                            items(
                                items = state.mappedMessage,
                                key = { item -> item.key },
                                contentType = { item -> item.key.startsWith("chat_") },
                            ) { item ->
                                if (item is MessageItem.Header) {
                                    MessageItemHeader(item.text)
                                } else if (item is MessageItem.MessageViewItem) {
                                    MessageItemContent(
                                        message = item.message,
                                        isMe = item.message.senderId == Firebase.auth.currentUser?.uid,
                                        onProfileClick = {
                                            onNavigateToProfile(Screen.ProfileScreen.route + "?userId=${item.message.senderId}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            MessageInputContainer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding(),
                messageText = messageText,
                onValueChange = { text -> messageText.value = text },
                onSendMessage = { text ->
                    messageText.value = ""
                    onSendMessage(title, text)
                }
            )
        }
    }
}