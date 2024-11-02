package com.djatar.ardath.core.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

private val placeholderColorLight = Color(red = 222, green = 216, blue = 225) // neutral87
private val placeholderHighlightColorLight = Color(red = 245, green = 239, blue = 247) // neutral95

private val placeholderColorDark = Color(red = 43, green = 41, blue = 48) // neutral17
private val placeholderHighlightColorDark = Color(red = 72, green = 70, blue = 76) // neutral30

@Composable
fun Modifier.placeHolderFade(visible: Boolean, darkMode: Boolean = isSystemInDarkTheme()): Modifier {
    return this.placeholder(
        visible = visible,
        color = if (!darkMode) placeholderColorLight else placeholderColorDark,
        highlight = PlaceholderHighlight.fade(
            highlightColor = if (!darkMode) placeholderHighlightColorLight
            else placeholderHighlightColorDark
        )
    )
}

@Composable
fun Modifier.placeHolderShimmer(visible: Boolean, darkMode: Boolean = isSystemInDarkTheme()): Modifier {
    return this.placeholder(
        visible = visible,
        color = if (!darkMode) placeholderColorLight else placeholderColorDark,
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = if (!darkMode) placeholderHighlightColorLight
            else placeholderHighlightColorDark
        )
    )
}