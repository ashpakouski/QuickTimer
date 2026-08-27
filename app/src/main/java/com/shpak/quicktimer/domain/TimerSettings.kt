package com.shpak.quicktimer.domain

data class TimerSettings(
    val dismissMode: DismissMode
)

enum class DismissMode {
    AUTOMATIC,
    MANUAL;
}