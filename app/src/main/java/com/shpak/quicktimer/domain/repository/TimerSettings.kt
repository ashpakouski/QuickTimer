package com.shpak.quicktimer.domain.repository

data class TimerSettings(
    val dismissMode: DismissMode
)

enum class DismissMode {
    AUTOMATIC,
    MANUAL;
}