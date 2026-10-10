package com.shpak.quicktimer.data.analytics

import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore

class LoggingTimerStore(
    private val delegate: TimerStore,
    private val analytics: AnalyticsLogger
) : TimerStore by delegate {

    override fun dispatch(event: TimerEvent) {
        val previous = delegate.state.value
        delegate.dispatch(event)

        if (delegate.state.value != previous) {
            analytics.log(event.toAnalyticsEvent(previous))
        }
    }
}

private fun TimerEvent.toAnalyticsEvent(state: TimerState): AnalyticsEvent = when (this) {
    is TimerEvent.Start -> AnalyticsEvent.TimerStart(durationMillis, state)
    is TimerEvent.AddTime -> AnalyticsEvent.TimerAddTime(durationMillis, state)
    TimerEvent.Pause -> AnalyticsEvent.TimerPause(state)
    TimerEvent.Resume -> AnalyticsEvent.TimerResume(state)
    TimerEvent.TimeUp -> AnalyticsEvent.TimerTimeUp(state)
    TimerEvent.Dismiss -> AnalyticsEvent.TimerDismiss(state)
    TimerEvent.Stop -> AnalyticsEvent.TimerStop(state)
}