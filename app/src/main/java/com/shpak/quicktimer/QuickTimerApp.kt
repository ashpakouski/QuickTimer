package com.shpak.quicktimer

import android.app.Application
import com.shpak.quicktimer.data.store.GlobalTimerStore
import com.shpak.quicktimer.di.Hub
import com.shpak.timer.core.store.TimerStore

class QuickTimerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Hub.addLazyInstance<TimerStore> {
            GlobalTimerStore()
        }
    }
}