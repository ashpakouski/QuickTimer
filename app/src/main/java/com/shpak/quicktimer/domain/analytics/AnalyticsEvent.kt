package com.shpak.quicktimer.domain.analytics

import com.shpak.quicktimer.domain.alarm.AlarmSound
import com.shpak.timer.core.TimerState

sealed class AnalyticsEvent(
    val name: String,
    val params: Map<AnalyticsParam, Any> = emptyMap()
) {
    class TimerStart(durationMillis: Long, state: TimerState) :
        AnalyticsEvent("timer_start", timerParams(state, durationMillis))

    class TimerAddTime(durationMillis: Long, state: TimerState) :
        AnalyticsEvent("timer_add_time", timerParams(state, durationMillis))

    class TimerPause(state: TimerState) : AnalyticsEvent("timer_pause", timerParams(state))
    class TimerResume(state: TimerState) : AnalyticsEvent("timer_resume", timerParams(state))
    class TimerTimeUp(state: TimerState) : AnalyticsEvent("timer_time_up", timerParams(state))
    class TimerDismiss(state: TimerState) : AnalyticsEvent("timer_dismiss", timerParams(state))
    class TimerStop(state: TimerState) : AnalyticsEvent("timer_stop", timerParams(state))

    class TileClick(state: TimerState) : AnalyticsEvent("tile_click", timerParams(state))

    class ScreenView(screenName: String) :
        AnalyticsEvent("screen_view", mapOf(AnalyticsParam.NAME to screenName))

    class AlarmSoundSelect(sound: AlarmSound) :
        AnalyticsEvent("alarm_sound_select", mapOf(AnalyticsParam.VALUE to sound.id))

    class AlarmSoundPreview(sound: AlarmSound) : AnalyticsEvent(
        name = "alarm_sound_preview",
        params = mapOf(AnalyticsParam.VALUE to sound.id)
    )

    data object NotificationRationaleShow : AnalyticsEvent("notification_rationale_show")

    class NotificationPermissionResult(isGranted: Boolean) : AnalyticsEvent(
        "notification_permission_result",
        mapOf(AnalyticsParam.VALUE to if (isGranted) "granted" else "denied")
    )

    data object NotificationSettingsOpen : AnalyticsEvent("notification_settings_open")
}

private fun timerParams(state: TimerState, durationMillis: Long? = null): Map<AnalyticsParam, Any> =
    buildMap {
        put(AnalyticsParam.STATE, state.analyticsString)
        durationMillis?.let { millis ->
            put(AnalyticsParam.DURATION, millis / 1_000L)
        }
    }

private val TimerState.analyticsString: String
    get() = when (this) {
        TimerState.Idle -> "idle"
        is TimerState.Running -> "running"
        is TimerState.Paused -> "paused"
        is TimerState.Ringing -> "ringing"
    }