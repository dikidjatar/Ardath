package com.djatar.ardath.feature.presentation.utils

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

// Date format constants
const val WEEKLY_DATE_FORMAT = "EEEE"
const val DEFAULT_DATE_FORMAT = "EEE, MMMM d"
const val EXTENDED_DATE_FORMAT = "EEE, MMM d, yyyy"
const val FULL_DATE_FORMAT = "EEEE, MMMM d, yyyy, hh:mm a"

fun Long.getDate(format: CharSequence = DEFAULT_DATE_FORMAT): String {
    return DateFormat.format(format, Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = this@getDate
    }).toString()
}

fun Long.getDate(
    format: CharSequence = DEFAULT_DATE_FORMAT,
    weeklyFormat: CharSequence = WEEKLY_DATE_FORMAT,
    extendedFormat: CharSequence = EXTENDED_DATE_FORMAT,
    stringToday: String,
    stringYesterday: String
): String {
    val currentDate = Calendar.getInstance(Locale.getDefault())
    val userDate = Calendar.getInstance(Locale.getDefault()).apply { timeInMillis = this@getDate }
    val daysDifference = (System.currentTimeMillis() - userDate.timeInMillis) / (1000 * 60 * 60 * 24)

    val hour = DateFormat.format("hh:mm a", userDate).toString()

    return when (daysDifference.toInt()) {
        0 -> if (currentDate.get(Calendar.DATE) != userDate.get(Calendar.DATE)) "$stringYesterday at $hour" else "$stringToday at $hour"
        1 -> "$stringYesterday at $hour"
        in 2..5 -> DateFormat.format(weeklyFormat, userDate).toString()
        else -> if (currentDate.get(Calendar.YEAR) > userDate.get(Calendar.YEAR))
            DateFormat.format(extendedFormat, userDate).toString()
        else
            DateFormat.format(format, userDate).toString()
    }
}