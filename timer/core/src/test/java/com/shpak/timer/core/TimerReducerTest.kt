package com.shpak.timer.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimerReducerTest {
    private val settings = TimerSettings(DismissMode.AUTOMATIC)

    @Test
    fun `start sets end time relative to now`() {
        val state = TimerReducer.reduce(
            TimerState.Idle, TimerEvent.Start(durationMillis = 5_000, settings = settings), nowMillis = 1_000
        )

        assertEquals(TimerState.Running(endTimeMillis = 6_000, settings = settings), state)
    }

    @Test
    fun `pause then resume keeps remaining time`() {
        val running = TimerState.Running(endTimeMillis = 6_000, settings = settings)

        val paused = TimerReducer.reduce(running, TimerEvent.Pause, nowMillis = 2_000)
        assertEquals(TimerState.Paused(remainingMillis = 4_000, settings = settings), paused)

        val resumed = TimerReducer.reduce(paused, TimerEvent.Resume, nowMillis = 10_000)
        assertEquals(TimerState.Running(endTimeMillis = 14_000, settings = settings), resumed)
    }

    @Test
    fun `time up before end time is ignored`() {
        val running = TimerState.Running(endTimeMillis = 6_000, settings = settings)

        assertEquals(running, TimerReducer.reduce(running, TimerEvent.TimeUp, nowMillis = 5_999))
    }

    @Test
    fun `time up at end time rings`() {
        val running = TimerState.Running(endTimeMillis = 6_000, settings = settings)

        assertEquals(
            TimerState.Ringing(settings),
            TimerReducer.reduce(running, TimerEvent.TimeUp, nowMillis = 6_000)
        )
    }

    @Test
    fun `dismiss only ends a ringing timer`() {
        val running = TimerState.Running(endTimeMillis = 6_000, settings = settings)

        assertEquals(running, TimerReducer.reduce(running, TimerEvent.Dismiss, nowMillis = 0))
        assertEquals(
            TimerState.Idle,
            TimerReducer.reduce(TimerState.Ringing(settings), TimerEvent.Dismiss, nowMillis = 0)
        )
    }

    @Test
    fun `stop resets any state`() {
        val paused = TimerState.Paused(remainingMillis = 1_000, settings = settings)

        assertEquals(TimerState.Idle, TimerReducer.reduce(paused, TimerEvent.Stop, nowMillis = 0))
    }
}