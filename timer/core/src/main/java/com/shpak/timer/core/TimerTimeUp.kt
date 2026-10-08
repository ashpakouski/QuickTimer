package com.shpak.timer.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun TimerStore.dispatchTimeUp(scope: CoroutineScope, clock: TimerClock): Job =
    scope.launch {
        state.collectLatest { current ->
            if (current !is TimerState.Running) {
                return@collectLatest
            }

            var remainingMillis = current.endTimeMillis - clock.nowMillis()
            while (remainingMillis > 0L) {
                delay(remainingMillis.milliseconds)
                remainingMillis = current.endTimeMillis - clock.nowMillis()
            }

            dispatch(TimerEvent.TimeUp)
        }
    }