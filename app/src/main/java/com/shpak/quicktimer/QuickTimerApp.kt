package com.shpak.quicktimer

import android.app.Application
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.presentation.TimerService
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.android.TimerAlarmScheduler
import com.shpak.timer.android.TimerStoreOwner
import com.shpak.timer.core.DefaultTimerStore
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.autoDismissRinging
import kotlinx.coroutines.MainScope

class QuickTimerApp : Application(), TimerStoreOwner {
    private val applicationScope = MainScope()

    override val timerStore: TimerStore by lazy {
        DefaultTimerStore(AndroidTimerClock)
    }

    override fun onCreate() {
        super.onCreate()

        Hub.addLazyInstance<TimerStore>(::timerStore)

        timerStore.autoDismissRinging(applicationScope)

        TimerAlarmScheduler(this).register(timerStore, applicationScope)
        TimerService.runWithActiveTimer(this, timerStore, applicationScope)
    }
}