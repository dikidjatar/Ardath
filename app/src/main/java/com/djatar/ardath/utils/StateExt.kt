package com.djatar.ardath.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.feature.presentation.profile.UserViewModel

@Composable
fun UserViewModel.ObserveUserState(onChange: UserViewModel.() -> Unit) {
    val state by user.collectAsStateWithLifecycle()
    LaunchedEffect(state) { onChange() }
}