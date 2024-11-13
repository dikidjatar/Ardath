/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.core.presentation.components.utils

sealed class Screen(val route: String) {
    data object ChatsScreen : Screen("chats_screen")
    data object ChatViewScreen : Screen("chat_view_screen") {
        fun idAndTitle() = "$route?userId={userId}&chatId={chatId}&title={title}"
    }

    data object ProfileScreen : Screen("profile_screen") {
        fun withId() = "$route?userId={userId}"
    }

    data object SignInScreen : Screen("signin_screen")
    data object SignUpScreen : Screen("signup_screen")

    data object Settings : Screen("settings")
    data object SettingsScreen : Screen("settings_screen")

    operator fun invoke() = route
}