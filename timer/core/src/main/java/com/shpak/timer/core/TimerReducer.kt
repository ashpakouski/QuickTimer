package com.shpak.timer.core

internal object TimerReducer {
    fun reduce(
        state: TimerState,
        event: TimerEvent,
        nowMillis: Long
    ): TimerState = when (event) {
        is TimerEvent.Start -> TimerState.Running(
            endTimeMillis = nowMillis + event.durationMillis,
            settings = event.settings,
            totalMillis = event.durationMillis
        )

        is TimerEvent.AddTime -> addTime(state, event.durationMillis, nowMillis)

        TimerEvent.Pause -> if (state is TimerState.Running) {
            TimerState.Paused(
                remainingMillis = (state.endTimeMillis - nowMillis).coerceAtLeast(0),
                settings = state.settings,
                totalMillis = state.totalMillis
            )
        } else {
            state
        }

        TimerEvent.Resume -> if (state is TimerState.Paused) {
            TimerState.Running(
                endTimeMillis = nowMillis + state.remainingMillis,
                settings = state.settings,
                totalMillis = state.totalMillis
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
        } else {
            state
        }

        TimerEvent.Stop -> TimerState.Idle
    }

    private fun addTime(
        state: TimerState,
        durationMillis: Long,
        nowMillis: Long
    ): TimerState {
        if (durationMillis <= 0L) {
            return state
        }

        return when (state) {
            TimerState.Idle -> state

            is TimerState.Running -> state.copy(
                endTimeMillis = state.endTimeMillis + durationMillis,
                totalMillis = state.totalMillis + durationMillis
            )

            is TimerState.Paused -> state.copy(
                remainingMillis = state.remainingMillis + durationMillis,
                totalMillis = state.totalMillis + durationMillis
            )

            is TimerState.Ringing -> TimerState.Running(
                endTimeMillis = nowMillis + durationMillis,
                settings = state.settings,
                totalMillis = durationMillis
            )
        }
    }
}