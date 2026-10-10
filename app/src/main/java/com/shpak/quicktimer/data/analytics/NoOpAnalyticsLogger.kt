package com.shpak.quicktimer.data.analytics

import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger

object NoOpAnalyticsLogger : AnalyticsLogger {
    override fun log(event: AnalyticsEvent) = Unit
    override fun logException(throwable: Throwable) = Unit
}