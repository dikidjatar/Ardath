package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable

@Stable
data class UserState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
