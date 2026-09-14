package com.shpak.timer.core.store

import com.shpak.timer.core.redux.TimerEvent
import com.shpak.timer.core.redux.TimerState
import kotlinx.coroutines.flow.StateFlow

interface TimerStore {
    fun observeGlobalTimer(): StateFlow<TimerState>
    fun onEvent(event: TimerEvent)
}