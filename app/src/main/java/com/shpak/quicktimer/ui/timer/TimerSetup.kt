package com.shpak.quicktimer.ui.timer

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class TimerSetup(
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0
) {
    val durationMillis: Long
        get() = ((hours * 60L + minutes) * 60L + seconds) * 1_000L

    val duration: Duration
        get() = durationMillis.milliseconds

    companion object {
        val HoursRange = 0..5
        val MinutesRange = 0..59
        val SecondsRange = 0..59
    }
}