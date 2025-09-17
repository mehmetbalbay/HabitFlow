package com.habitflow.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitflow.feature.auth.R
import com.habitflow.feature.auth.presentation.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String) -> Unit,
    onNavigateLogin: () -> Unit,
    onDismissFeedback: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthIllustration(
            icon = Icons.Default.Person,
            backgroundColor = Color(0xFFE8F5E9),
            tint = MaterialTheme.colorScheme.primary,
            title = stringResource(id = R.string.register_title),
            subtitle = stringResource(id = R.string.register_subtitle)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = stringResource(id = R.string.email_label),
            leadingIcon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = stringResource(id = R.string.password_label),
            leadingIcon = Icons.Default.Person,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        FilledTonalButton(
            onClick = { onRegister(email.value, password.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.register_button))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.register_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onNavigateLogin) {
            Text(text = stringResource(id = R.string.register_login_here))
        }
    }
}

