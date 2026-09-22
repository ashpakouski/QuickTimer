package com.shpak.timer.core

data class TimerSettings(
    val dismissMode: DismissMode
)

enum class DismissMode {
    AUTOMATIC,
    MANUAL;
}