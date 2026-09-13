package com.shpak.quicktimer.domain.store

import com.shpak.quicktimer.domain.repository.TimerEvent
import com.shpak.quicktimer.domain.repository.TimerState
import kotlinx.coroutines.flow.StateFlow

interface TimerStore {
    fun observeGlobalTimer(): StateFlow<TimerState>
    fun onEvent(event: TimerEvent)
}