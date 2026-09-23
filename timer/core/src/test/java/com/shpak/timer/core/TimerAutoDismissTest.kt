package com.shpak.timer.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerAutoDismissTest {

    @Test
    fun `automatic timer stops ringing on its own`() = runTest {
        val store = ringingStore(DismissMode.AUTOMATIC)

        advanceTimeBy(3_001)
        runCurrent()

        assertEquals(TimerState.Idle, store.state.value)
    }

    @Test
    fun `manual timer keeps ringing`() = runTest {
        val store = ringingStore(DismissMode.MANUAL)

        advanceTimeBy(60_000)
        runCurrent()

        assertEquals(TimerState.Ringing(TimerSettings(DismissMode.MANUAL)), store.state.value)
    }

    private fun TestScope.ringingStore(dismissMode: DismissMode): TimerStore {
        val store = DefaultTimerStore(TimerClock { testScheduler.currentTime })

        store.autoDismissRinging(backgroundScope, afterMillis = 3_000)
        runCurrent()

        store.dispatch(
            TimerEvent.Start(durationMillis = 1_000, settings = TimerSettings(dismissMode))
        )
        advanceTimeBy(1_000)
        runCurrent()

        store.dispatch(TimerEvent.TimeUp)
        runCurrent()

        return store
    }
}