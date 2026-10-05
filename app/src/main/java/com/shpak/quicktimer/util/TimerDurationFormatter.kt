package com.shpak.quicktimer.util

import java.util.Locale

fun Long.toTimerSeconds(): Long {
    val millis = coerceAtLeast(0L)
    return millis / 1_000L + if (millis % 1_000L == 0L) 0L else 1L
}

fun Long.toTimerDisplay(locale: Locale = Locale.getDefault()): String {
    val seconds = toTimerSeconds()
    val hours = seconds / 3_600L
    val minutes = seconds / 60L % 60L
    return if (hours > 0L) {
        String.format(locale, "%d:%02d:%02d", hours, minutes, seconds % 60L)
    } else {
        String.format(locale, "%d:%02d", minutes, seconds % 60L)
    }
}