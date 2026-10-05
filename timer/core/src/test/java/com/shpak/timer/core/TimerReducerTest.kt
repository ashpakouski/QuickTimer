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

        assertEquals(TimerState.Running(endTimeMillis = 6_000, settings = settings, totalMillis = 5_000), state)
    }

    @Test
    fun `pause then resume keeps remaining time`() {
        val running = TimerState.Running(endTimeMillis = 6_000, settings = settings, totalMillis = 5_000)

        val paused = TimerReducer.reduce(running, TimerEvent.Pause, nowMillis = 2_000)
        assertEquals(TimerState.Paused(remainingMillis = 4_000, settings = settings, totalMillis = 5_000), paused)

        val resumed = TimerReducer.reduce(paused, TimerEvent.Resume, nowMillis = 10_000)
        assertEquals(TimerState.Running(endTimeMillis = 14_000, settings = settings, totalMillis = 5_000), resumed)
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

    @Test
    fun `adding time extends the deadline and total and ignores the old alarm`() {
        val running = TimerState.Running(6_000, settings, totalMillis = 5_000)
        val extended = TimerReducer.reduce(running, TimerEvent.AddTime(60_000), nowMillis = 2_000)
        assertEquals(TimerState.Running(66_000, settings, totalMillis = 65_000), extended)
        assertEquals(extended, TimerReducer.reduce(extended, TimerEvent.TimeUp, nowMillis = 6_000))
        assertEquals(TimerState.Ringing(settings), TimerReducer.reduce(extended, TimerEvent.TimeUp, nowMillis = 66_000))
    }

    @Test
    fun `adding time while paused preserves pause through resume`() {
        val paused = TimerState.Paused(4_000, settings, totalMillis = 5_000)
        val extended = TimerReducer.reduce(paused, TimerEvent.AddTime(60_000), nowMillis = 2_000)
        assertEquals(TimerState.Paused(64_000, settings, totalMillis = 65_000), extended)
        assertEquals(
            TimerState.Running(74_000, settings, totalMillis = 65_000),
            TimerReducer.reduce(extended, TimerEvent.Resume, nowMillis = 10_000)
        )
    }

    @Test
    fun `adding time to ringing starts a fresh countdown`() {
        assertEquals(
            TimerState.Running(70_000, settings, totalMillis = 60_000),
            TimerReducer.reduce(TimerState.Ringing(settings), TimerEvent.AddTime(60_000), nowMillis = 10_000)
        )
    }

    @Test
    fun `repeated additions accumulate`() {
        val running = TimerState.Running(6_000, settings, totalMillis = 5_000)
        val once = TimerReducer.reduce(running, TimerEvent.AddTime(60_000), nowMillis = 2_000)
        val twice = TimerReducer.reduce(once, TimerEvent.AddTime(60_000), nowMillis = 3_000)
        assertEquals(TimerState.Running(126_000, settings, totalMillis = 125_000), twice)
    }

    @Test
    fun `adding time is ignored at idle and for nonpositive durations`() {
        val running = TimerState.Running(6_000, settings, totalMillis = 5_000)
        assertEquals(TimerState.Idle, TimerReducer.reduce(TimerState.Idle, TimerEvent.AddTime(60_000), 1_000))
        for (duration in listOf(0L, -1L)) {
            assertEquals(running, TimerReducer.reduce(running, TimerEvent.AddTime(duration), 1_000))
        }
    }
}