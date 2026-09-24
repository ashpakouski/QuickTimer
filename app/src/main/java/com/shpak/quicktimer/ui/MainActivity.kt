package com.shpak.quicktimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme
import com.shpak.quicktimer.ui.timer.TimerScreen
import com.shpak.quicktimer.ui.timer.TimerViewModel

class MainActivity : ComponentActivity() {
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            QuickTimerTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    TimerScreen(viewModel = timerViewModel)
                }
            }
        }
    }
}