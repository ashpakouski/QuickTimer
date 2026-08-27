package com.shpak.quicktimer.domain

sealed interface TimerEvent {
    data class Start(
        val durationMillis: Long,
        val settings: TimerSettings
    ) : TimerEvent

    data object Pause : TimerEvent
    data object Resume : TimerEvent
    data object TimeUp : TimerEvent
    data object Stop : TimerEvent
}