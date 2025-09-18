package com.habitflow.feature.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExerciseCard(
    modifier: Modifier = Modifier,
    vm: ExerciseViewModel = hiltViewModel()
) {
    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val totalMin = sessions.sumOf { it.durationMin }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Egzersiz: ${totalMin} dk (bu ay)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
            FilledTonalButton(onClick = { vm.quickLog("Yürüyüş", 20) }) { Text("+20 dk") }
            FilledTonalButton(onClick = { vm.quickLog("Esneme", 10) }) { Text("+10 dk") }
        }
        Spacer(Modifier.height(12.dp))
    }
}

