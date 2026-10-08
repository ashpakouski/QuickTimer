package com.shpak.quicktimer.domain.alarm

import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerSettings

fun timerSettings(isContinuousPlayback: Boolean): TimerSettings = TimerSettings(
    dismissMode = if (isContinuousPlayback) {
        DismissMode.MANUAL
    } else {
        DismissMode.AUTOMATIC
    }
)