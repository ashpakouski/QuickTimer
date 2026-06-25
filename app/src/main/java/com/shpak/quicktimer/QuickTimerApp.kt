package com.shpak.quicktimer

import android.app.Application
import com.shpak.quicktimer.data.timer.ElapsedRealtimeTimerClock
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.timer.DefaultTimerRepository
import com.shpak.quicktimer.domain.timer.TimerClock
import com.shpak.quicktimer.domain.timer.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class QuickTimerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Hub.addLazyInstance<TimerClock> {
            ElapsedRealtimeTimerClock()
        }

        Hub.addLazyInstance<TimerRepository> {
            DefaultTimerRepository(
                clock = Hub.get<TimerClock>(),
                scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            )
        }
    }
}