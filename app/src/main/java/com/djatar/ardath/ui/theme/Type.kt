package com.djatar.ardath.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection

val Typography =
    Typography().run {
        copy(
            bodyLarge = bodyLarge.applyLineBreak().applyTextDirection(),
            bodyMedium = bodyMedium.applyLineBreak().applyTextDirection(),
            bodySmall = bodySmall.applyLineBreak().applyTextDirection(),
            labelLarge = labelLarge.applyTextDirection(),
            labelMedium = labelMedium.applyTextDirection(),
            labelSmall = labelSmall.applyTextDirection(),
            titleLarge = titleLarge.applyTextDirection(),
            titleMedium = titleMedium.applyTextDirection(),
            titleSmall = titleSmall.applyTextDirection(),
            headlineLarge = headlineLarge.applyTextDirection(),
            headlineMedium = headlineMedium.applyTextDirection(),
            headlineSmall = headlineSmall.applyTextDirection(),
            displayLarge = displayLarge.applyTextDirection(),
            displayMedium = displayMedium.applyTextDirection(),
            displaySmall = displaySmall.applyTextDirection()
        )
    }

private fun TextStyle.applyLineBreak(): TextStyle = this.copy(lineBreak = LineBreak.Paragraph)
private fun TextStyle.applyTextDirection(): TextStyle =
    this.copy(textDirection = TextDirection.Content)