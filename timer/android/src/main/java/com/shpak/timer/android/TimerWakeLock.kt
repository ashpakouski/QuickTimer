package com.shpak.timer.android

import android.content.Context
import android.os.PowerManager
import com.shpak.timer.core.TimerClock
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val ALARM_MIN_FUTURITY_MILLIS = 5_000L
private const val RELEASE_MARGIN_MILLIS = 1_500L
private const val TAG = "QuickTimer:TimerWakeLock"

class TimerWakeLock(
    context: Context,
    private val clock: TimerClock
) {
    private val wakeLock = context.applicationContext
        .getSystemService(PowerManager::class.java)
        ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        ?.apply {
            setReferenceCounted(false)
        }

    fun register(store: TimerStore, scope: CoroutineScope): Job = scope.launch {
        store.state.collect(::update)
    }

    private fun update(state: TimerState) {
        val wakeLock = wakeLock ?: return
        val remainingMillis = (state as? TimerState.Running)?.let { runningState ->
            runningState.endTimeMillis - clock.nowMillis()
        }

        if (remainingMillis != null && remainingMillis <= ALARM_MIN_FUTURITY_MILLIS) {
            wakeLock.acquire(remainingMillis.coerceAtLeast(0L) + RELEASE_MARGIN_MILLIS)
        } else if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}