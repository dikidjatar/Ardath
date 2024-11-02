package com.djatar.ardath.feature.domain.models

data class UserState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
