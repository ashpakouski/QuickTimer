package com.shpak.quicktimer.domain.alarm

interface SoundPreviewPlayer {
    fun play(sound: AlarmSound, onComplete: () -> Unit)
    fun stop()
}