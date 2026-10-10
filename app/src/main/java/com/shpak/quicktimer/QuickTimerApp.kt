package com.shpak.quicktimer

import android.app.Application
import com.shpak.quicktimer.data.alarm.DefaultAlarmSettingsRepository
import com.shpak.quicktimer.data.alarm.MediaPlayerAlarmPlayer
import com.shpak.quicktimer.data.analytics.AnalyticsLoggerFactory
import com.shpak.quicktimer.data.analytics.LoggingTimerStore
import com.shpak.quicktimer.data.notification.AndroidNotificationPermission
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.alarm.AlarmPlayer
import com.shpak.quicktimer.domain.alarm.AlarmSettingsRepository
import com.shpak.quicktimer.domain.alarm.SoundPreviewPlayer
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.quicktimer.domain.notification.NotificationPermission
import com.shpak.quicktimer.presentation.TimerService
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.android.TimerAlarmScheduler
import com.shpak.timer.android.TimerStoreOwner
import com.shpak.timer.android.TimerWakeLock
import com.shpak.timer.core.DefaultTimerStore
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.autoDismissRinging
import com.shpak.timer.core.dispatchTimeUp
import kotlinx.coroutines.MainScope

class QuickTimerApp : Application(), TimerStoreOwner {
    private val applicationScope = MainScope()

    private val analytics: AnalyticsLogger by lazy {
        AnalyticsLoggerFactory.create(this)
    }

    override val timerStore: TimerStore by lazy {
        LoggingTimerStore(DefaultTimerStore(AndroidTimerClock), analytics)
    }

    override fun onCreate() {
        super.onCreate()

        Hub.addLazyInstance<AnalyticsLogger>(::analytics)
        Hub.addLazyInstance<TimerStore>(::timerStore)
        Hub.addLazyInstance<AlarmSettingsRepository> { DefaultAlarmSettingsRepository(this) }
        Hub.addFactory<AlarmPlayer> { MediaPlayerAlarmPlayer(this) }
        Hub.addFactory<SoundPreviewPlayer> { MediaPlayerAlarmPlayer(this) }
        Hub.addLazyInstance<NotificationPermission> { AndroidNotificationPermission(this) }

        timerStore.dispatchTimeUp(applicationScope, AndroidTimerClock)
        timerStore.autoDismissRinging(applicationScope)

        TimerAlarmScheduler(this).register(timerStore, applicationScope)
        TimerWakeLock(this, AndroidTimerClock).register(timerStore, applicationScope)
        TimerService.runWithActiveTimer(this, timerStore, applicationScope)
    }
}