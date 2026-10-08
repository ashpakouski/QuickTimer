package com.shpak.quicktimer.ui.timer

import androidx.lifecycle.ViewModel
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.Countdown
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.countdown
import com.shpak.timer.core.remainingMillisAt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimerViewModel(
    private val timerStore: TimerStore = Hub.get<TimerStore>()
) : ViewModel() {
    private val _setup = MutableStateFlow(TimerSetup())
    val setup: StateFlow<TimerSetup> = _setup.asStateFlow()

    val countdown = timerStore.countdown(
        clock = AndroidTimerClock,
        tickMillis = 100L
    )

    fun currentCountdown(): Countdown {
        val state = timerStore.state.value
        return Countdown(state, state.remainingMillisAt(AndroidTimerClock.nowMillis()))
    }

    fun onStart() {
        timerStore.dispatch(
            TimerEvent.Start(
                durationMillis = _setup.value.durationMillis,
                settings = TimerSettings(
                    dismissMode = DismissMode.MANUAL
                )
            )
        )
    }

    fun onEvent(event: TimerEvent) {
        timerStore.dispatch(event)
    }

    fun onSetupChange(transform: (TimerSetup) -> TimerSetup) {
        _setup.update(transform)
    }
}