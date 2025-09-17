package com.habitflow.feature.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onShowToast: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is HomeEffect.Toast) onShowToast(effect.message)
        }
    }
    Button(onClick = { viewModel.onEvent(HomeEvent.OnActionClicked) }) {
        Text(text = state.greeting)
    }
}

