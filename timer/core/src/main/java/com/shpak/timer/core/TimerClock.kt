package com.shpak.timer.core

fun interface TimerClock {
    fun nowMillis(): Long
}