package com.djatar.ardath.feature.presentation.utils

import com.tencent.mmkv.MMKV

const val DARK_THEME = "dark_theme"

const val IS_CHAT_ON = "is_chat_on"
const val CHAT_USER_ID = "chat_user_id"

private val mmkv: MMKV = MMKV.defaultMMKV()

object PreferenceUtil {
    fun String.getBoolean(default: Boolean = false): Boolean =
        mmkv.decodeBool(this, default)
    fun String.getString(default: String = ""): String =
        mmkv.decodeString(this) ?: default

    fun String.updateBoolean(newValue: Boolean) = mmkv.encode(this, newValue)
    fun String.updateString(newString: String) = mmkv.encode(this, newString)

    fun encodeString(key: String, string: String) = key.updateString(string)
    fun updateValue(key: String, b: Boolean) = key.updateBoolean(b)
    fun getValue(key: String): Boolean = key.getBoolean()
    fun removeValue(key: String) = mmkv.removeValueForKey(key)
    fun contains(key: String) = mmkv.contains(key)
}