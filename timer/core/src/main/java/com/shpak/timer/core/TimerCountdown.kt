package com.shpak.timer.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

data class Countdown(
    val state: TimerState,
    val remainingMillis: Long
)

fun TimerState.remainingMillisAt(nowMillis: Long): Long = when (this) {
    is TimerState.Running -> (endTimeMillis - nowMillis).coerceAtLeast(0)
    is TimerState.Paused -> remainingMillis
    is TimerState.Ringing, TimerState.Idle -> 0L
}

// FIXME
fun TimerStore.countdown(clock: TimerClock, tickMillis: Long = 1_000L): Flow<Countdown> =
    channelFlow {
        state.collectLatest { current ->
            while (true) {
                val remaining = current.remainingMillisAt(clock.nowMillis())
                send(Countdown(current, remaining))

                if (current !is TimerState.Running || remaining == 0L) break

                val untilNextBoundary = remaining % tickMillis
                delay(if (untilNextBoundary == 0L) tickMillis else untilNextBoundary)
            }
        }
    }