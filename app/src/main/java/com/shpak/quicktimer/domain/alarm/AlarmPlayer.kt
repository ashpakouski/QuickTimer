package com.shpak.quicktimer.domain.alarm

interface AlarmPlayer {
    fun play(sound: AlarmSound, isLooping: Boolean)
    fun stop()
}