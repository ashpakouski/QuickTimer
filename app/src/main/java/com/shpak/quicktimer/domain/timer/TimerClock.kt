package com.shpak.quicktimer.domain.timer

interface TimerClock {
    fun nowMillis(): Long
}