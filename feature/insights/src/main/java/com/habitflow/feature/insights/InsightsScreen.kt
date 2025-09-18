package com.habitflow.feature.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.core.ui.chart.DonutProgress
import com.habitflow.core.ui.chart.LineChart

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Insights", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Seri", style = MaterialTheme.typography.titleMedium)
                Text("${state.streak} gün", style = MaterialTheme.typography.headlineSmall)
                Text(state.motivationBadge, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Haftalık Uyum", style = MaterialTheme.typography.titleMedium)
                DonutProgress(
                    progress = state.weeklyCompliancePercent / 100f,
                    label = "${state.weeklyCompliancePercent}%"
                )
                if (state.weeklyComplianceSpark.isNotEmpty()) {
                    LineChart(points = state.weeklyComplianceSpark)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryChip(title = "Su", value = "${state.waterTodayMl} ml", modifier = Modifier.weight(1f))
            SummaryChip(title = "Egzersiz", value = "${state.exerciseMinutes} dk", modifier = Modifier.weight(1f))
            SummaryChip(title = "Öğün", value = "${state.mealsToday}", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

