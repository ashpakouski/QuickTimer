package com.shpak.timer.core

sealed interface TimerEvent {
    data class Start(
        val durationMillis: Long,
        val settings: TimerSettings
    ) : TimerEvent

    data object Pause : TimerEvent
    data object Resume : TimerEvent
    data object TimeUp : TimerEvent
    data object Dismiss : TimerEvent
    data object Stop : TimerEvent
}