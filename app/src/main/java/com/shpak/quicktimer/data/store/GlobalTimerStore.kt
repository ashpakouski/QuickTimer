package com.shpak.quicktimer.data.store

import android.os.SystemClock
import com.shpak.timer.core.redux.TimerEvent
import com.shpak.timer.core.store.TimerStore
import com.shpak.timer.core.redux.TimerReducer
import com.shpak.timer.core.redux.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GlobalTimerStore : TimerStore {
    private val timerFlow = MutableStateFlow<TimerState>(TimerState.Idle)
    private val _timerFlow = timerFlow.asStateFlow()

    override fun observeGlobalTimer(): StateFlow<TimerState> = _timerFlow

    override fun onEvent(event: TimerEvent) {
        timerFlow.update { timerState ->
            TimerReducer.reduce(
                timerState, event, SystemClock.elapsedRealtime()
            )
        }
    }
}