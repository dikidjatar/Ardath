package com.djatar.ardath.feature.domain.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val status: String = "offline",
    val lastSeen: Long = 0L
)
