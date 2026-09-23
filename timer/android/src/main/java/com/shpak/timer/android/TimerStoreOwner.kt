package com.shpak.timer.android

import com.shpak.timer.core.TimerStore

interface TimerStoreOwner {
    val timerStore: TimerStore
}