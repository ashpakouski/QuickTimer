package com.shpak.quicktimer.ui.timer

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.core.redux.TimerEvent
import com.shpak.timer.core.store.TimerStore
import com.shpak.timer.core.redux.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TimerViewModel(
    private val timerStore: TimerStore = Hub.get<TimerStore>()
) : ViewModel() {
    private val _remainingMillis = MutableStateFlow(0L)
    val remainingMillis = _remainingMillis.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            timerStore.observeGlobalTimer().collect { timerState ->
                onStateChanged(timerState)
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        timerStore.onEvent(event)
//        val newState = TimerReducer.reduce(_state.value, event, nowMillis())
//        _state.value = newState
//        onStateChanged(newState)
    }

    private fun onStateChanged(state: TimerState) {
        timerJob?.cancel()
        timerJob = null

        when (state) {
            is TimerState.Running -> {
                timerJob = viewModelScope.launch {
//                viewModelScope.launch {
                    while (isActive) {
                        val remainingMillis = state.remainingMillis()
                        _remainingMillis.value = remainingMillis

                        if (remainingMillis <= 0) {
                            onEvent(TimerEvent.TimeUp)
                            return@launch
                        }

                        delay(100L.milliseconds)
                    }
                }
            }

            is TimerState.Paused -> _remainingMillis.value = state.remainingMillis
            is TimerState.Ringing -> _remainingMillis.value = 0
            TimerState.Idle -> _remainingMillis.value = 0
        }
    }

    private fun TimerState.Running.remainingMillis(): Long =
        (endTimeMillis - nowMillis()).coerceAtLeast(0)

    private fun nowMillis(): Long = SystemClock.elapsedRealtime()
}