package com.habitflow.feature.water

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
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
import com.habitflow.core.ui.chart.DonutProgress
import kotlin.math.roundToInt

@Composable
fun WaterCard(
    modifier: Modifier = Modifier,
    viewModel: WaterViewModel = hiltViewModel()
) {
    val entries by viewModel.todayEntries.collectAsStateWithLifecycle()
    val total = entries.sumOf { it.amountMl }
    val goal = viewModel.dailyGoalMl
    val progress = if (goal > 0) total.toFloat() / goal else 0f
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Su",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        DonutProgress(
            progress = progress,
            label = "${(progress.coerceIn(0f,1f) * 100).roundToInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "$total / $goal ml",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            FilledTonalButton(onClick = { viewModel.quickAdd(200) }) { Text("+200 ml") }
            FilledTonalButton(onClick = { viewModel.quickAdd(300) }) { Text("+300 ml") }
            FilledTonalButton(onClick = { viewModel.quickAdd(500) }) { Text("+500 ml") }
        }
        Spacer(Modifier.height(12.dp))
    }
}
