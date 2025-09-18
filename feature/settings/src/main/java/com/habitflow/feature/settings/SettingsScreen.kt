package com.habitflow.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ayarlar", style = MaterialTheme.typography.headlineMedium)

        CardItem(title = "Tema", subtitle = "Sistem (M3)")
        CardItem(title = "Sessiz Saatler", subtitle = "22:00 - 07:00")
        CardItem(title = "Birimler", subtitle = "Su: L, Enerji: kcal")
        CardItem(title = "Veri Yedekleme", subtitle = "Google Drive")
        CardItem(title = "Dil", subtitle = "Türkçe")
        CardItem(title = "Gizlilik", subtitle = "Cihaz içi, Fit verisi isteğe bağlı")
    }
}

@Composable
private fun CardItem(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

