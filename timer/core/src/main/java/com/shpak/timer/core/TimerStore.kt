package com.shpak.timer.core

import kotlinx.coroutines.flow.StateFlow

interface TimerStore {
    val state: StateFlow<TimerState>
    fun dispatch(event: TimerEvent)
}