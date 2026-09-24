package com.shpak.timer.android

import android.os.SystemClock
import com.shpak.timer.core.TimerClock

object AndroidTimerClock : TimerClock {
    override fun nowMillis(): Long = SystemClock.elapsedRealtime()
}