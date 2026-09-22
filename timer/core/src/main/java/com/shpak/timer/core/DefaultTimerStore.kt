package com.shpak.timer.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultTimerStore(
    private val clock: TimerClock
) : TimerStore {
    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    override val state: StateFlow<TimerState> = _state.asStateFlow()

    override fun dispatch(event: TimerEvent) {
        _state.update { current ->
            TimerReducer.reduce(current, event, clock.nowMillis())
        }
    }
}