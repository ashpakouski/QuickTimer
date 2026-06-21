package com.shpak.quicktimer.data.timer

import android.os.SystemClock
import com.shpak.quicktimer.domain.timer.TimerClock

class ElapsedRealtimeTimerClock : TimerClock {
    override fun nowMillis(): Long = SystemClock.elapsedRealtime()
}