package com.shpak.timer.core

sealed interface TimerState {
    data object Idle : TimerState

    data class Running(
        val endTimeMillis: Long,
        val settings: TimerSettings,
        val totalMillis: Long = 0L
    ) : TimerState

    data class Paused(
        val remainingMillis: Long,
        val settings: TimerSettings,
        val totalMillis: Long = 0L
    ) : TimerState

    data class Ringing(
        val settings: TimerSettings
    ) : TimerState
}