package com.djatar.ardath.feature.presentation.utils

import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.djatar.ardath.ArdathApp.Companion.applicationScope
import com.djatar.ardath.ArdathApp.Companion.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastUtil {
    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeToast(resId: Int) {
        makeToast(context.getString(resId))
    }

    fun makeToastSuspend(text: String) {
        applicationScope.launch(Dispatchers.Main) {
            makeToast(text)
        }
    }
}

fun Char?.getColor(): Color {
    return colorMap[this] ?: Color(0xFA585858)
}

private val colorMap: Map<Char, Color> =
    mapOf(
        'A' to Color(0xFFF99000),
        'B' to Color(0xFAE761FF),
        'C' to Color(0xFA61BBFF),
        'D' to Color(0xFA6CFF61),
        'D' to Color(0xFAFF6161),
        'E' to Color(0xFAF4FF61),
        'F' to Color(0xFA6C61FF),
        'G' to Color(0xFFF99000),
        'H' to Color(0xFAE761FF),
        'I' to Color(0xFA61BBFF),
        'J' to Color(0xFA6CFF61),
        'K' to Color(0xFAFF6161),
        'L' to Color(0xFAF4FF61),
        'M' to Color(0xFA6C61FF),
        'N' to Color(0xFFF99000),
        'O' to Color(0xFAE761FF),
        'P' to Color(0xFA61BBFF),
        'Q' to Color(0xFA6CFF61),
        'R' to Color(0xFAFF6161),
        'S' to Color(0xFAF4FF61),
        'T' to Color(0xFA6C61FF),
        'U' to Color(0xFFF99000),
        'V' to Color(0xFAE761FF),
        'W' to Color(0xFA61BBFF),
        'X' to Color(0xFA6CFF61),
        'Y' to Color(0xFAFF6161),
        'Z' to Color(0xFAF4FF61),
    )