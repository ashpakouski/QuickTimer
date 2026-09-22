package com.shpak.quicktimer

import android.app.Application
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.timerStore

class QuickTimerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Hub.addLazyInstance<TimerStore> {
            timerStore(AndroidTimerClock)
        }
    }
}