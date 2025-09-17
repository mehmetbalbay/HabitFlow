package com.habitflow.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.habitflow.feature.auth.R
import com.habitflow.feature.auth.presentation.AuthUiState

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onSend: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onDismissFeedback: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthIllustration(
            icon = Icons.Default.Email,
            backgroundColor = Color(0xFFFFF8E1),
            tint = MaterialTheme.colorScheme.primary,
            title = stringResource(id = R.string.forgot_title),
            subtitle = stringResource(id = R.string.forgot_subtitle)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = stringResource(id = R.string.email_label),
            leadingIcon = Icons.Default.Email
        )
        Spacer(modifier = Modifier.height(20.dp))
        FilledTonalButton(
            onClick = { onSend(email.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.forgot_button))
        }
    }
}

