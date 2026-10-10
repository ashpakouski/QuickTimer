package com.shpak.quicktimer.domain.analytics

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun logException(throwable: Throwable)
}