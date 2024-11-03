/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.feature.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.CloseButton
import com.djatar.ardath.core.presentation.components.DeleteButton
import com.djatar.ardath.core.presentation.components.DeleteDialog
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.domain.models.Chat
import com.djatar.ardath.feature.domain.models.ChatState
import com.djatar.ardath.feature.presentation.chatview.components.ChatItem
import com.djatar.ardath.feature.presentation.chatview.components.ChatItemLoader
import com.djatar.ardath.feature.presentation.chatview.components.NewChatDialog
import com.djatar.ardath.feature.presentation.common.components.EmptyChat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "ChatScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    chatViewModel: ChatViewModel,
    chatState: StateFlow<ChatState>,
    paddingValues: PaddingValues,
    isScrolling: MutableState<Boolean>,
    selectionState: MutableState<Boolean>,
    selectedChatState: SnapshotStateList<Chat>,
    toggleSelection: (Int) -> Unit,
    onLoadMore: (batchSize: Int) -> Unit = {},
    onNavigateToChatView: (route: String) -> Unit,
    onNavigateToProfile: (route: String) -> Unit = {},
    onLogout: () -> Unit,
) {
    val state by chatState.collectAsStateWithLifecycle()

    var showNewChatDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val lazyListState = rememberLazyListState()

    val viewPortHeight by remember { derivedStateOf { lazyListState.layoutInfo.viewportSize.height } }
    val itemHeight = with(density) { 86.dp.toPx() }
    val batchSize = (viewPortHeight / itemHeight).toInt()

    val reachedBottom by remember { derivedStateOf { lazyListState.reachedBottom() } }
    if (reachedBottom) { onLoadMore(batchSize) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

//    LaunchedEffect(key1 = lazyListState) {
//        snapshotFlow { lazyListState.isScrolledToEnd() && lazyListState.canScrollBackward }
//            .distinctUntilChanged()
//            .collectLatest { isAtEnd ->
//                if (isAtEnd) {
//                    chatViewModel.loadChats(batchSize)
//                }
//            }
//    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        isScrolling.value = lazyListState.isScrollInProgress
    }

    BackHandler(selectionState.value && selectedChatState.isNotEmpty()) {
        chatViewModel.clearSelection()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text(
                    text = if (selectionState.value) selectedChatState.size.toString()
                    else stringResource(R.string.app_name)
                ) },
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    if (!selectionState.value) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.settings)) },
                            leadingIcon = { Icon(imageVector = Icons.Outlined.Settings, contentDescription = null) },
                            onClick = {}
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.logout, Firebase.auth.currentUser?.displayName ?: "")) },
                            leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                            onClick = onLogout
                        )
                    }

                    if (selectionState.value) {
                        DeleteButton(selectedChatState.isNotEmpty()) {
                            showDeleteDialog = true
                        }
                    }
                },
                navigationIcon = {
                    if (selectionState.value) {
                        CloseButton { chatViewModel.clearSelection() }
                    }
                }
            )
        },
        floatingActionButton = {
            val floatingActionAlpha by animateFloatAsState(
                targetValue = if (isScrolling.value || selectionState.value) 0f else 1f,
                label = "floatingActionAlpha"
            )
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .alpha(floatingActionAlpha)
                    .padding(bottom = paddingValues.calculateBottomPadding() + 70.dp),
                text = { Text(text = stringResource(R.string.new_chat)) },
                icon = { Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = null) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    chatViewModel.setSelectedChatId(null)
                    showNewChatDialog = true
                },
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (!state.isLoading && state.chats.isEmpty()) {
                EmptyChat()
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (!isScrolling.value) paddingValues.calculateBottomPadding() + 70.dp else 0.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.chats) { chatItem ->
                        ChatItem(
                            isLoading = false,
                            chat = chatItem,
                            selectionState = selectionState,
                            selectedChatState = selectedChatState,
                            onClick = { chat ->
                                if (!selectionState.value && selectedChatState.isEmpty()) {
                                    chatViewModel.setSelectedChatId(chat.id)
                                    val params = "?userId=${chat.userId}&chatId=${chat.id}&title=${chat.title}"
                                    onNavigateToChatView(Screen.ChatViewScreen.route + params)
                                } else {
                                    toggleSelection(state.chats.indexOf(chat))
                                }
                            },
                            onLongClick = { chat ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                toggleSelection(state.chats.indexOf(chat))
                            },
                            onProfileClick = {
                                onNavigateToProfile(Screen.ProfileScreen.route + "?userId=${chatItem.userId}")
                            }
                        )
                    }
                    if (state.isLoading) {
                        items(batchSize) { ChatItemLoader() }
                    }
                }
            }
        }
    }

    NewChatDialog(
        showDialog = showNewChatDialog,
        viewModel = chatViewModel,
        chatId = state.selectedChatId,
        onItemClick = onNavigateToChatView
    ) { showNewChatDialog = false }

    DeleteDialog(
        showDialog = showDeleteDialog,
        title = stringResource(R.string.delete_chats, selectedChatState.size),
        message = stringResource(R.string.delete_chats_desc),
        onCancel = { showDeleteDialog = false },
    ) {
        selectionState.value = false
        showDeleteDialog = false
        chatViewModel.deleteChat(selectedChatState) {
            chatViewModel.clearSelection()
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (it) context.getString(R.string.delete_chats_success)
                    else context.getString(R.string.delete_chats_error)
                )
            }
        }
    }
}

private fun LazyListState.isScrolledToEnd(): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return false
    return lastVisibleItemIndex == layoutInfo.totalItemsCount - 1
}

private fun LazyListState.reachedBottom(buffer: Int = 0): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}