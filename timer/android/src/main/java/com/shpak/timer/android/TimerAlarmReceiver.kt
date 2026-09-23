package com.shpak.timer.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shpak.timer.core.TimerEvent

class TimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TIME_UP) {
            return
        }

        (context.applicationContext as? TimerStoreOwner)
            ?.timerStore
            ?.dispatch(TimerEvent.TimeUp)
    }

    internal companion object {
        const val ACTION_TIME_UP = "com.shpak.timer.android.action.TIME_UP"
    }
}