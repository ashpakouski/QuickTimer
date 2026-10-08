package com.shpak.quicktimer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shpak.quicktimer.ui.settings.SettingsScreen
import com.shpak.quicktimer.ui.settings.SettingsViewModel
import com.shpak.quicktimer.ui.timer.TimerScreen
import com.shpak.quicktimer.ui.timer.TimerViewModel
import kotlinx.serialization.Serializable

@Serializable
private data object TimerRoute

@Serializable
private data object SettingsRoute

@Composable
fun QuickTimerNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TimerRoute,
        modifier = modifier
    ) {
        composable<TimerRoute> {
            TimerScreen(
                viewModel = viewModel<TimerViewModel>(),
                onSettingsClick = { navController.navigate(SettingsRoute) }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = viewModel<SettingsViewModel>(),
                onNavigateUp = navController::navigateUp
            )
        }
    }
}