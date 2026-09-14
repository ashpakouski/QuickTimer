package com.shpak.timer.core.redux

data class TimerSettings(
    val dismissMode: DismissMode
)

enum class DismissMode {
    AUTOMATIC,
    MANUAL;
}