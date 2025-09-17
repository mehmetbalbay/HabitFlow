package com.habitflow.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitflow.feature.auth.R
import com.habitflow.feature.auth.presentation.AuthFeedbackType
import com.habitflow.feature.auth.presentation.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateForgot: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onDismissFeedback: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val feedback = uiState.feedback
    val feedbackMessage = feedback?.messageRes?.let { stringResource(id = it) } ?: feedback?.messageText.orEmpty()
    val isInputError = feedback?.type == AuthFeedbackType.ERROR && feedback.titleRes == R.string.auth_feedback_input_title

    LaunchedEffect(feedback) {
        if (feedback != null) {
            scrollState.animateScrollTo(0)
            if (feedbackMessage.isNotBlank() && feedback.type != AuthFeedbackType.SUCCESS) {
                snackbarHostState.showSnackbar(
                    message = feedbackMessage,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = feedback != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AuthFeedbackBanner(
                    feedback = feedback,
                    onDismiss = onDismissFeedback,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            AuthIllustration(
                icon = Icons.Default.Person,
                backgroundColor = Color(0xFFE3F2FD),
                tint = MaterialTheme.colorScheme.primary,
                title = stringResource(id = R.string.login_title),
                subtitle = stringResource(id = R.string.login_subtitle)
            )
            Spacer(modifier = Modifier.height(24.dp))
            AuthTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = stringResource(id = R.string.email_label),
                leadingIcon = Icons.Default.Email,
                isPassword = false,
                isError = isInputError,
                supportingText = if (isInputError) stringResource(id = R.string.auth_feedback_input_body) else null
            )
            Spacer(modifier = Modifier.height(12.dp))
            AuthTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = stringResource(id = R.string.password_label),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = isInputError,
                supportingText = if (isInputError) stringResource(id = R.string.auth_feedback_input_body) else null
            )
            Spacer(modifier = Modifier.height(20.dp))
            FilledTonalButton(
                onClick = { onLogin(email.value, password.value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(id = R.string.login_button))
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    Text(text = stringResource(id = R.string.google_sign_in))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButtonSmall(
                text = stringResource(id = R.string.login_forgot_password),
                onClick = onNavigateForgot
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.login_no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            FilledTonalButton(
                onClick = onNavigateRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(id = R.string.login_register_here))
            }
        }
    }
}

@Composable
private fun TextButtonSmall(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(text = text)
    }
}

