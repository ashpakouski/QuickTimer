package com.shpak.quicktimer.util

import android.media.AudioManager
import android.os.Build

fun AudioManager.volumeStepFraction(): Float {
    val volumeMin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        getStreamMinVolume(AudioManager.STREAM_ALARM) else 0
    val volumeMax = getStreamMaxVolume(AudioManager.STREAM_ALARM)

    return 1f / (volumeMax - volumeMin).toFloat()
}

fun AudioManager.currentVolumeFraction(): Float {
    val volumeMin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        getStreamMinVolume(AudioManager.STREAM_ALARM) else 0
    val volumeMax = getStreamMaxVolume(AudioManager.STREAM_ALARM)
    val volumeCurrent = getStreamVolume(AudioManager.STREAM_ALARM)

    return (volumeCurrent - volumeMin) / (volumeMax - volumeMin).toFloat()
}