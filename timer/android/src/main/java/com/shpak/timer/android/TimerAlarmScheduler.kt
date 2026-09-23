package com.shpak.timer.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val REQUEST_CODE = 420

class TimerAlarmScheduler(context: Context) {
    private val context = context.applicationContext
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun register(store: TimerStore, scope: CoroutineScope): Job = scope.launch {
        store.state.collect(::updateAlarm)
    }

    private fun updateAlarm(state: TimerState) {
        val alarmManager = alarmManager ?: return
        val pendingIntent = pendingIntent()

        if (state !is TimerState.Running) {
            alarmManager.cancel(pendingIntent)
            return
        }

        if (canScheduleExactAlarms(alarmManager)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, state.endTimeMillis, pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, state.endTimeMillis, pendingIntent
            )
        }
    }

    private fun pendingIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, TimerAlarmReceiver::class.java).setAction(TimerAlarmReceiver.ACTION_TIME_UP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun canScheduleExactAlarms(alarmManager: AlarmManager): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
}