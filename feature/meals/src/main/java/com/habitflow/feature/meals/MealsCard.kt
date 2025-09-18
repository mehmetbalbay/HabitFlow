package com.habitflow.feature.meals

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
fun MealsCard(
    modifier: Modifier = Modifier,
    vm: MealsViewModel = hiltViewModel()
) {
    val meals by vm.todayMeals.collectAsStateWithLifecycle()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Öğünler: ${meals.size} (bugün)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
            FilledTonalButton(onClick = { vm.quickAdd("Kahvaltı") }) { Text("Kahvaltı") }
            FilledTonalButton(onClick = { vm.quickAdd("Öğle") }) { Text("Öğle") }
            FilledTonalButton(onClick = { vm.quickAdd("Akşam") }) { Text("Akşam") }
        }
        Spacer(Modifier.height(12.dp))
    }
}

