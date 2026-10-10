package com.shpak.quicktimer.data.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.quicktimer.domain.analytics.AnalyticsParam

class FirebaseAnalyticsLogger(context: Context) : AnalyticsLogger {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.params.toBundle())
    }

    override fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}

private fun Map<AnalyticsParam, Any>.toBundle() = Bundle().apply {
    forEach { (param, value) ->
        val key = param.key
        putString(key, "$value")
    }
}