package com.shpak.quicktimer.data.notification

import android.content.Context
import com.shpak.quicktimer.domain.notification.NotificationPermission
import com.shpak.quicktimer.util.areNotificationsEnabled

class AndroidNotificationPermission(context: Context) : NotificationPermission {
    private val context = context.applicationContext

    override fun isGranted(): Boolean = areNotificationsEnabled(context)
}