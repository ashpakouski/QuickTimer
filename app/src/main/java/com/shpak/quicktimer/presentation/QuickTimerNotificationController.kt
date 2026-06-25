package com.shpak.quicktimer.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.domain.timer.TimerState
import com.shpak.quicktimer.util.getNotificationButton
import com.shpak.quicktimer.util.toHhMmSs

class QuickTimerNotificationController(
    private val context: Context
) {
    companion object {
        const val NOTIFICATION_ID = 7

        const val ACTION_PAUSE = "action_pause"
        const val ACTION_RESUME = "action_resume"
        const val ACTION_CANCEL = "action_cancel"
    }

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val pauseButton by lazy {
        getNotificationButton(context, ACTION_PAUSE, R.string.notification_button_pause)
    }
    private val resumeButton by lazy {
        getNotificationButton(context, ACTION_RESUME, R.string.notification_button_resume)
    }
    private val cancelButton by lazy {
        getNotificationButton(context, ACTION_CANCEL, R.string.notification_button_cancel)
    }

    private val baseNotificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat
            .Builder(context, context.getString(R.string.notification_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)

    init {
        createNotificationChannel()
    }

    fun getNotification(
        message: String,
        actions: List<NotificationCompat.Action> = emptyList()
    ): Notification = baseNotificationBuilder.setContentTitle(message).apply {
        actions.forEach(::addAction)
    }.build()

    fun render(state: TimerState) {
        when (state) {
            is TimerState.Running -> post(
                state.remainingMillis.toHhMmSs(), listOf(cancelButton, pauseButton)
            )

            is TimerState.Paused -> post(
                state.remainingMillis.toHhMmSs(), listOf(cancelButton, resumeButton)
            )

            TimerState.Finished -> post(0L.toHhMmSs())

            TimerState.Idle -> Unit
        }
    }

    private fun post(
        message: String,
        actions: List<NotificationCompat.Action> = emptyList()
    ) {
        notificationManager.notify(NOTIFICATION_ID, getNotification(message, actions))
    }

    private fun createNotificationChannel() {
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                context.getString(R.string.notification_channel_id),
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        )
    }
}