package com.shpak.quicktimer.data.analytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger

object AnalyticsLoggerFactory {
    fun create(context: Context): AnalyticsLogger {
        // Firebase is not initialized when google-services.json is missing
        if (FirebaseApp.getApps(context).isEmpty()) {
            return NoOpAnalyticsLogger
        }

        return FirebaseAnalyticsLogger(context)
    }
}