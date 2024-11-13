package com.djatar.ardath.feature.presentation.auth.components

import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R

data object AuthError {
    const val EMPTY_EMAIL = 1
    const val EMPTY_PASSWORD = 2
    const val CONFIRM_PASSWORD = 3
    const val EMPTY_NAME = 4

    const val SAVE_USER_DATA = 10

    fun Int.getErrorText(): String =
        when (this) {
            EMPTY_EMAIL -> context.getString(R.string.empty_email_error)
            EMPTY_PASSWORD -> context.getString(R.string.empty_password_error)
            CONFIRM_PASSWORD -> context.getString(R.string.confirm_password_error)
            EMPTY_NAME -> context.getString(R.string.empty_name_error)
            SAVE_USER_DATA -> context.getString(R.string.save_user_data_error)
            else -> "Error"
        }
}