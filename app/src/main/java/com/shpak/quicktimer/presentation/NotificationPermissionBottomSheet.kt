package com.shpak.quicktimer.presentation

import android.content.Context
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.quicktimer.ui.permission.NotificationPermissionContent
import com.shpak.quicktimer.util.redirectToNotificationSettings

class NotificationPermissionBottomSheet(context: Context) : ComposeBottomSheet(context) {
    init {
        setSheetContent {
            NotificationPermissionContent(
                onConfirm = ::onOpenSettingsClick,
                onDismiss = ::dismiss,
                shouldShowCloseButton = true
            )
        }
    }

    private fun onOpenSettingsClick() {
        Hub.get<AnalyticsLogger>().log(AnalyticsEvent.NotificationSettingsOpen)
        redirectToNotificationSettings(context.applicationContext)
        dismiss()
    }
}