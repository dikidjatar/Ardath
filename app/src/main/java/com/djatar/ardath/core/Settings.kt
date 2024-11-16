package com.djatar.ardath.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class SettingsType {
    data object Switch : SettingsType()
    data object Header : SettingsType()
    data object TextField : SettingsType()
    data object Default : SettingsType()
}

sealed class Position {
    data object Top : Position()
    data object Middle : Position()
    data object Bottom : Position()
    data object Alone : Position()
}

sealed class SettingsEntity(
    open val icon: ImageVector? = null,
    open val title: String = "",
    open val summary: String? = null,
    val type: SettingsType = SettingsType.Default,
    open val enabled: Boolean = true,
    open val isChecked: Boolean? = null,
    open val onChecked: ((Boolean) -> Unit)? = null,
    open val onClick: (() -> Unit)? = null,
    open val screenPosition: Position = Position.Alone,
    open val containerColor: Color? = null,
    open val titleContentColor: Color? = null,
    open val value: String = "",
    open val onValueChange: (String) -> Unit = {},
    open val label: String? = null,
    open val maxLines: Int = Int.MAX_VALUE,
) {
    val isHeader = type == SettingsType.Header

    data class Header(override val title: String) : SettingsEntity(
        title = title,
        type = SettingsType.Header
    )

    data class Preference(
        override val icon: ImageVector? = null,
        override val title: String,
        override val summary: String? = null,
        override val enabled: Boolean = true,
        override val screenPosition: Position = Position.Alone,
        override val onClick: (() -> Unit)? = null,
        override val containerColor: Color? = null,
        override val titleContentColor: Color? = null
    ) : SettingsEntity(
        icon = icon,
        title = title,
        summary = summary,
        enabled = enabled,
        screenPosition = screenPosition,
        onClick = onClick,
        containerColor = containerColor,
        titleContentColor = titleContentColor
    )

    data class SwitchPreference(
        override val icon: ImageVector? = null,
        override val title: String,
        override val summary: String? = null,
        override val enabled: Boolean = true,
        override val screenPosition: Position = Position.Alone,
        override val isChecked: Boolean = false,
        override val onChecked: ((Boolean) -> Unit)? = null,
    ) : SettingsEntity(
        icon = icon,
        title = title,
        summary = summary,
        enabled = enabled,
        isChecked = isChecked,
        onChecked = onChecked,
        screenPosition = screenPosition,
        type = SettingsType.Switch
    )

    data class TextField(
        override val value: String,
        override val onValueChange: (String) -> Unit,
        override val label: String? = null,
        override val maxLines: Int = Int.MAX_VALUE,
        override val summary: String? = null,
        override val enabled: Boolean = true,
        override val screenPosition: Position = Position.Alone
    ) : SettingsEntity(
        value = value,
        onValueChange = onValueChange,
        label = label,
        maxLines = maxLines,
        summary = summary,
        enabled = enabled,
        screenPosition = screenPosition,
        type = SettingsType.TextField
    )
}