package com.djatar.ardath.feature.domain.models

data class Chat(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
