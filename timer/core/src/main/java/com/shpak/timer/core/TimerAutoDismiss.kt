package com.shpak.timer.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun TimerStore.autoDismissRinging(scope: CoroutineScope, afterMillis: Long = 3_000L): Job =
    scope.launch {
        state.collectLatest { current ->
            if (current !is TimerState.Ringing) {
                return@collectLatest
            }

            if (current.settings.dismissMode != DismissMode.AUTOMATIC) {
                return@collectLatest
            }

            delay(afterMillis.milliseconds)

            dispatch(TimerEvent.Dismiss)
        }
    }