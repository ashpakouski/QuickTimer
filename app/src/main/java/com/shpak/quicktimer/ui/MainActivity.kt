package com.shpak.quicktimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.shpak.quicktimer.ui.timer.TimerScreen
import com.shpak.quicktimer.ui.timer.TimerViewModel

class MainActivity : ComponentActivity() {
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TimerScreen(viewModel = timerViewModel)
        }
    }
}