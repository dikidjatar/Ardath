package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable

@Stable
data class UserState(
    val users: List<User> = emptyList(),
    val currentUser: User? = null,
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null
)

@Stable
data class UpdateUserState<T>(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: T? = null
)

@Stable
data class UsernameState(
    val isUsernameTaken: Boolean? = null,
    val isChecking: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)