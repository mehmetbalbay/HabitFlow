package com.habitflow.app

import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun AppScaffold(navController: NavHostController) {
    val context = LocalContext.current
    Scaffold { _ ->
        AppNavHost(
            navController = navController,
            onShowToast = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
        )
    }
}

