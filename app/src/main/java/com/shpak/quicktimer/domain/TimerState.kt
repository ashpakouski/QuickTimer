package com.shpak.quicktimer.domain

sealed interface TimerState {
    data object Idle : TimerState

    data class Running(
        val endTimeMillis: Long,
        val settings: TimerSettings
    ) : TimerState

    data class Paused(
        val remainingMillis: Long,
        val settings: TimerSettings
    ) : TimerState

    data class Ringing(
        val settings: TimerSettings
    ) : TimerState
}