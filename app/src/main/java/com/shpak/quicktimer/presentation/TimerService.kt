package com.shpak.quicktimer.presentation

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.util.lazyTryOrNull
import com.shpak.quicktimer.util.playSound
import com.shpak.quicktimer.util.toHhMmSs
import com.shpak.timer.android.AndroidTimerClock
import com.shpak.timer.core.Countdown
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore
import com.shpak.timer.core.countdown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TimerService : Service() {
    companion object {
        private const val ACTION_PAUSE = "com.shpak.quicktimer.action.PAUSE"
        private const val ACTION_RESUME = "com.shpak.quicktimer.action.RESUME"
        private const val ACTION_STOP = "com.shpak.quicktimer.action.STOP"
        private const val ACTION_DISMISS = "com.shpak.quicktimer.action.DISMISS"

        private const val NOTIFICATION_ID = 7

        fun runWithActiveTimer(context: Context, store: TimerStore, scope: CoroutineScope): Job =
            scope.launch {
                store.state.collect { state ->
                    if (state !is TimerState.Idle) {
                        start(context)
                    }
                }
            }

        private fun start(context: Context) {
            try {
                context.startForegroundService(Intent(context, TimerService::class.java))
            } catch (_: Exception) {
                Toast.makeText(
                    context, R.string.error_cant_start_timer_service, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val timerStore by lazy { Hub.get<TimerStore>() }
    private val serviceScope = MainScope()

    private val notificationController by lazyTryOrNull {
        QuickTimerNotificationController(applicationContext)
    }

    private val pauseButton by lazyTryOrNull {
        notificationButton(ACTION_PAUSE, R.string.notification_button_pause)
    }

    private val resumeButton by lazyTryOrNull {
        notificationButton(ACTION_RESUME, R.string.notification_button_resume)
    }

    private val cancelButton by lazyTryOrNull {
        notificationButton(ACTION_STOP, R.string.notification_button_cancel)
    }

    private val dismissButton by lazyTryOrNull {
        notificationButton(ACTION_DISMISS, R.string.notification_button_dismiss)
    }

    private var renderedState: TimerState? = null

    override fun onCreate() {
        super.onCreate()

        notificationController?.getNotification("")?.let {
            startForeground(NOTIFICATION_ID, it)
        }

        serviceScope.launch {
            timerStore.countdown(AndroidTimerClock).collect(::render)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> timerStore.dispatch(TimerEvent.Pause)
            ACTION_RESUME -> timerStore.dispatch(TimerEvent.Resume)
            ACTION_STOP -> timerStore.dispatch(TimerEvent.Stop)
            ACTION_DISMISS -> timerStore.dispatch(TimerEvent.Dismiss)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun render(countdown: Countdown) {
        val state = countdown.state
        val time = countdown.remainingMillis.toHhMmSs()

        when (state) {
            TimerState.Idle -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            is TimerState.Running -> postNotification(
                message = time,
                actions = listOfNotNull(cancelButton, pauseButton)
            )

            is TimerState.Paused -> postNotification(
                message = time,
                actions = listOfNotNull(cancelButton, resumeButton)
            )

            is TimerState.Ringing -> {
                val isManual = state.settings.dismissMode == DismissMode.MANUAL

                postNotification(
                    message = time,
                    actions = if (isManual) {
                        listOfNotNull(dismissButton)
                    } else {
                        emptyList()
                    }
                )

                if (renderedState !is TimerState.Ringing) {
                    ring()
                }
            }
        }

        renderedState = state
    }

    private fun postNotification(message: String, actions: List<NotificationCompat.Action>) {
        notificationController?.postNotification(
            NOTIFICATION_ID, message, actions
        )
    }

    private fun ring() {
        try {
            playSound(applicationContext, R.raw.double_ping)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notificationButton(action: String, @StringRes titleId: Int) =
        NotificationCompat.Action(
            0,
            getString(titleId),
            PendingIntent.getForegroundService(
                this,
                0,
                Intent(this, TimerService::class.java).setAction(action),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
}