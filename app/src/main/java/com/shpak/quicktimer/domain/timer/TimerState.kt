package com.shpak.quicktimer.domain.timer

sealed interface TimerState {
    data object Idle : TimerState
    data class Running(val remainingMillis: Long, val totalMillis: Long) : TimerState
    data class Paused(val remainingMillis: Long, val totalMillis: Long) : TimerState
    data object Finished : TimerState
}