package com.shpak.quicktimer.domain.timer

import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val state: StateFlow<TimerState>

    fun start(durationMillis: Long)
    fun pause()
    fun resume()
    fun cancel()

    /**
     * Forces an immediate transition to [TimerState.Finished] if a countdown is
     * active. Used by the platform alarm fallback.
     */
    fun notifyTimeReached()
}