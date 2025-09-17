package com.habitflow.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.habitflow.core.designsystem.theme.HabitFlowTheme

@Composable
fun App() {
    HabitFlowTheme {
        val navController = rememberNavController()
        AppScaffold(navController)
    }
}
