package com.shpak.quicktimer.presentation

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shpak.quicktimer.R
import com.shpak.quicktimer.data.AlarmVolumeTracker
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.ui.timer.TimerSetup
import com.shpak.quicktimer.ui.timer.TimerSetupUi
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore

class TimerSetupBottomSheet(context: Context) : ComposeBottomSheet(context) {

    companion object {
        private const val MIN_AUDIBLE_VOLUME_FRACTION = 0.35f
    }

    private var setup by mutableStateOf(TimerSetup())
    private var isVolumeLow by mutableStateOf(false)

    private val volumeTracker = AlarmVolumeTracker(context, ::onVolumeFractionChange)
    private val timerStore = Hub.get<TimerStore>()

    init {
        setSheetContent {
            TimerSetupUi(
                setup = setup,
                isVolumeLow = isVolumeLow,
                onSetupChange = ::onSetupChange,
                onStart = ::onStartClick,
                onCancel = ::dismiss
            )
        }

        setOnShowListener { onShow() }
        setOnDismissListener { onDismiss() }
        setOnKeyListener(volumeTracker)
        volumeControlStream = AudioManager.STREAM_ALARM
    }

    private fun onShow() {
        QuantKeeper.start(context)

        volumeTracker.start()
        volumeTracker.currentVolume?.let(::onVolumeFractionChange)
    }

    private fun onDismiss() {
        QuantKeeper.stop(context)

        volumeTracker.stop()
    }

    private fun onStartClick() {
        if (timerStore.state.value is TimerState.Idle) {
            timerStore.dispatch(
                TimerEvent.Start(
                    durationMillis = setup.durationMillis,
                    settings = TimerSettings(
                        dismissMode = DismissMode.MANUAL
                    )
                )
            )
        } else {
            Toast.makeText(
                context, R.string.error_timer_is_already_running, Toast.LENGTH_LONG
            ).show()
        }

        dismiss()
    }

    private fun onSetupChange(transform: (TimerSetup) -> TimerSetup) {
        setup = transform(setup)
    }

    private fun onVolumeFractionChange(volumeFraction: Float) {
        isVolumeLow = volumeFraction < MIN_AUDIBLE_VOLUME_FRACTION
    }
}