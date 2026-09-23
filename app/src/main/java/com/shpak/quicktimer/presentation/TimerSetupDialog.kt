package com.shpak.quicktimer.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.shpak.quicktimer.R
import com.shpak.quicktimer.data.MediaVolumeTracker
import com.shpak.quicktimer.databinding.TimerSettingsDialogBinding
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.util.HapticsCompat
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerState
import com.shpak.timer.core.TimerStore

class TimerSetupDialog(context: Context) : CustomDialog(context) {

    companion object {
        private const val MIN_AUDIBLE_VOLUME_FRACTION = 0.35f
    }

    private val volumeTracker = MediaVolumeTracker(context, ::onVolumeFractionChange)
    private val haptics = HapticsCompat(context)
    private val timerStore = Hub.get<TimerStore>()

    private val binding = TimerSettingsDialogBinding.inflate(
        LayoutInflater.from(context), null, false
    )

    private val currentSelectionMillis: Long
        get() {
            val hours = binding.hoursPicker.value
            val minutes = binding.minutesPicker.value
            val seconds = binding.secondsPicker.value

            return (hours * 60 * 60 + minutes * 60 + seconds) * 1000L
        }

    init {
        setContentView(binding.root)

        setOnShowListener { onShow() }
        setOnDismissListener { onDismiss() }
        setOnKeyListener(volumeTracker)
    }

    private fun onShow() {
        QuantKeeper.start(context)

        setupPickers(::onPickerStateChange)

        binding.buttonPositive.setOnClickListener { onButtonPositiveClick() }
        binding.buttonNegative.setOnClickListener { onButtonNegativeClick() }

        volumeTracker.start()
        volumeTracker.currentVolume?.let(::onVolumeFractionChange)
    }

    private fun onDismiss() {
        QuantKeeper.stop(context)

        volumeTracker.stop()
    }

    private fun onButtonPositiveClick() {
        if (timerStore.state.value is TimerState.Idle) {
            timerStore.dispatch(
                TimerEvent.Start(
                    durationMillis = currentSelectionMillis,
                    settings = TimerSettings(
                        dismissMode = DismissMode.AUTOMATIC
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

    private fun onButtonNegativeClick() {
        dismiss()
    }

    private fun onPickerStateChange() {
        haptics.generateSingleTick()
        binding.buttonPositive.isEnabled = currentSelectionMillis != 0L
    }

    private fun onVolumeFractionChange(volumeFraction: Float) {
        binding.warningView.visibility =
            if (volumeFraction < MIN_AUDIBLE_VOLUME_FRACTION) View.VISIBLE else View.GONE
    }

    private fun setupPickers(onPickerStateChange: () -> Unit) {
        binding.hoursPicker.apply {
            maxValue = 11
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }

        binding.minutesPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }

        binding.secondsPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }
    }
}