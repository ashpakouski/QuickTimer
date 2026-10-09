package com.shpak.quicktimer.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.notification.NotificationPermission
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.Countdown
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.countdown
import com.shpak.timer.core.remainingMillisAt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TimerViewModel(
    private val timerStore: TimerStore = Hub.get<TimerStore>(),
    private val notificationPermission: NotificationPermission = Hub.get<NotificationPermission>()
) : ViewModel() {
    private val _setup = MutableStateFlow(TimerSetup())
    private val _isNotificationRationaleVisible = MutableStateFlow(false)

    val uiState: StateFlow<TimerUiState> = combine(
        timerStore.countdown(clock = AndroidTimerClock, tickMillis = 100L),
        _setup,
        _isNotificationRationaleVisible
    ) { countdown, setup, isNotificationRationaleVisible ->
        TimerUiState(
            countdown = countdown,
            setup = setup,
            isNotificationRationaleVisible = isNotificationRationaleVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TimerUiState(countdown = currentCountdown())
    )

    private fun currentCountdown(): Countdown {
        val state = timerStore.state.value
        return Countdown(state, state.remainingMillisAt(AndroidTimerClock.nowMillis()))
    }

    fun onStart() {
        if (!notificationPermission.isGranted()) {
            _isNotificationRationaleVisible.value = true
            return
        }

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
        if (event is TimerEvent.Resume && !notificationPermission.isGranted()) {
            _isNotificationRationaleVisible.value = true
            return
        }

        timerStore.dispatch(event)
    }

    fun onNotificationRationaleConfirm() {
        _isNotificationRationaleVisible.value = false
    }

    fun onNotificationRationaleDismiss() {
        _isNotificationRationaleVisible.value = false
    }

    fun onSetupChange(transform: (TimerSetup) -> TimerSetup) {
        _setup.update(transform)
    }
}