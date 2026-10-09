package com.shpak.quicktimer.ui.timer

import com.shpak.timer.core.Countdown

data class TimerUiState(
    val countdown: Countdown,
    val setup: TimerSetup = TimerSetup(),
    val isNotificationRationaleVisible: Boolean = false
)