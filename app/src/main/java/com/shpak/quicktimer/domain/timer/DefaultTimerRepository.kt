package com.shpak.quicktimer.domain.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DefaultTimerRepository(
    private val clock: TimerClock,
    private val scope: CoroutineScope,
    private val tickIntervalMillis: Long = 1000L
) : TimerRepository {

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    override val state: StateFlow<TimerState> = _state.asStateFlow()

    private var tickJob: Job? = null
    private var endAtMillis = 0L
    private var totalMillis = 0L

    override fun start(durationMillis: Long) {
        totalMillis = durationMillis
        endAtMillis = clock.nowMillis() + durationMillis
        launchCountdown()
    }

    override fun pause() {
        val current = _state.value
        if (current !is TimerState.Running) {
            return
        }

        tickJob?.cancel()
        tickJob = null
        _state.value = TimerState.Paused(current.remainingMillis, current.totalMillis)
    }

    override fun resume() {
        val current = _state.value
        if (current !is TimerState.Paused) {
            return
        }

        endAtMillis = clock.nowMillis() + current.remainingMillis
        launchCountdown()
    }

    override fun cancel() {
        tickJob?.cancel()
        tickJob = null
        _state.value = TimerState.Idle
    }

    override fun notifyTimeReached() {
        if (_state.value is TimerState.Running) {
            finish()
        }
    }

    private fun launchCountdown() {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                val remainingMillis = endAtMillis - clock.nowMillis()
                if (remainingMillis <= 0) {
                    finish()
                    return@launch
                }
                _state.value = TimerState.Running(remainingMillis, totalMillis)
                delay(tickIntervalMillis)
            }
        }
    }

    private fun finish() {
        tickJob?.cancel()
        tickJob = null
        _state.value = TimerState.Finished
    }
}