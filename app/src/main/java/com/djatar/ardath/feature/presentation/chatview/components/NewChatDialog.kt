/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.presentation.common.ChatViewModel
import com.djatar.ardath.feature.presentation.profile.UserViewModel

private const val TAG = "NewChatDialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatDialog(
    showDialog: Boolean,
    viewModel: ChatViewModel,
    chatId: String?,
    onItemClick: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    if (showDialog && !chatId.isNullOrEmpty()) {
        val userViewModel = hiltViewModel<UserViewModel>().also {
            it.getUsers()
        }
        val state by userViewModel.userState.collectAsState()

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false
            )
        ) {
            Scaffold(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(10.dp)),
                topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.new_chat)) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                            }
                        }
                    )
                }
            ) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    items(state.users) { user ->
                        UserItem(
                            user.name,
                            user.status,
                            onClick = {
                                viewModel.hasChat(user.id) { id ->
                                    val params = "?userId=${user.id}&chatId=${id ?: chatId}&title=${user.name}"
                                    onItemClick(Screen.ChatViewScreen.route + params)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}