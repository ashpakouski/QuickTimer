package com.shpak.quicktimer.data.analytics

import android.content.Context
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger

object AnalyticsLoggerFactory {
    fun create(context: Context): AnalyticsLogger = NoOpAnalyticsLogger
}