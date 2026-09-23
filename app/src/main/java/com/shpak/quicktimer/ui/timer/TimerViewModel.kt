package com.shpak.quicktimer.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.countdown
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TimerViewModel(
    private val timerStore: TimerStore = Hub.get<TimerStore>()
) : ViewModel() {
    val remainingMillis: StateFlow<Long> = timerStore
        .countdown(
            clock = AndroidTimerClock,
            tickMillis = 100L
        )
        .map { countdown ->
            countdown.remainingMillis
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    fun onEvent(event: TimerEvent) {
        timerStore.dispatch(event)
    }
}