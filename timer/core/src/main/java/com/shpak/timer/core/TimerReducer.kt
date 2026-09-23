package com.shpak.timer.core

internal object TimerReducer {
    fun reduce(
        state: TimerState,
        event: TimerEvent,
        nowMillis: Long
    ): TimerState = when (event) {
        is TimerEvent.Start -> TimerState.Running(
            endTimeMillis = nowMillis + event.durationMillis,
            settings = event.settings
        )

        TimerEvent.Pause -> if (state is TimerState.Running) {
            TimerState.Paused(
                remainingMillis = (state.endTimeMillis - nowMillis).coerceAtLeast(0),
                settings = state.settings
            )
        } else {
            state
        }

        TimerEvent.Resume -> if (state is TimerState.Paused) {
            TimerState.Running(
                endTimeMillis = nowMillis + state.remainingMillis,
                settings = state.settings
            )
        } else {
            state
        }

        TimerEvent.TimeUp -> if (state is TimerState.Running && nowMillis >= state.endTimeMillis) {
            TimerState.Ringing(state.settings)
        } else {
            state
        }

        TimerEvent.Dismiss -> if (state is TimerState.Ringing) {
            TimerState.Idle
        }else {
            state
        }

        TimerEvent.Stop -> TimerState.Idle
    }
}