package com.shpak.quicktimer.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.countdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val timerStore: TimerStore = Hub.get<TimerStore>()
) : ViewModel() {
    private val _remainingMillis = MutableStateFlow(0L)
    val remainingMillis = _remainingMillis.asStateFlow()

    init {
        viewModelScope.launch {
            timerStore.countdown(AndroidTimerClock, tickMillis = 100L).collect { countdown ->
                _remainingMillis.value = countdown.remainingMillis

                // Nothing outside this screen drives the timer yet, so expiry is reported here
                if (countdown.state is TimerState.Running && countdown.remainingMillis == 0L) {
                    timerStore.dispatch(TimerEvent.TimeUp)
                }
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        timerStore.dispatch(event)
    }
}