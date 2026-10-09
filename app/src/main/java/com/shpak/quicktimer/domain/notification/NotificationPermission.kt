package com.shpak.quicktimer.domain.notification

interface NotificationPermission {
    fun isGranted(): Boolean
}