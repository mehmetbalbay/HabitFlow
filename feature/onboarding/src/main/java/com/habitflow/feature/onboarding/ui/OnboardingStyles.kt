package com.habitflow.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class OnboardingColors(
    val gradientStart: Color,
    val gradientEnd: Color,
    val cardBackground: Color,
    val accent: Color,
    val faintOnBackground: Color
)

data class OnboardingDimens(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cardElevation: Dp
)

private val DefaultColors = OnboardingColors(
    gradientStart = Color(0xFF78C3FB),
    gradientEnd = Color(0xFF4FD4C3),
    cardBackground = Color.White,
    accent = Color(0xFF1C9E92),
    faintOnBackground = Color.White.copy(alpha = 0.7f)
)

private val DefaultDimens = OnboardingDimens(
    horizontalPadding = 24.dp,
    verticalPadding = 20.dp,
    cardElevation = 16.dp
)

val LocalOnboardingColors = staticCompositionLocalOf { DefaultColors }
val LocalOnboardingDimens = staticCompositionLocalOf { DefaultDimens }

@Composable
fun OnboardingTheme(
    colors: OnboardingColors = DefaultColors,
    dimens: OnboardingDimens = DefaultDimens,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalOnboardingColors provides colors,
        LocalOnboardingDimens provides dimens,
        content = content
    )
}

data class OnboardingPrimaryAction(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)

data class OnboardingSecondaryAction(
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun OnboardingScreenScaffold(
    modifier: Modifier = Modifier,
    step: Int,
    totalSteps: Int,
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    primaryAction: OnboardingPrimaryAction,
    secondaryAction: OnboardingSecondaryAction? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalOnboardingColors.current
    val dimens = LocalOnboardingDimens.current
    val horizontalPadding = dimens.horizontalPadding
    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ) {
                if (onBack != null) {
                    IconButton(
                        modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.16f)),
                        onClick = onBack
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            }
            SpacerHeight(16.dp)
            LinearProgressIndicator(
                progress = step / totalSteps.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                color = colors.accent,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            SpacerHeight(24.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = dimens.cardElevation),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Adım $step / $totalSteps",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.accent
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        subtitle?.let {
                            Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Column(content = content)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = primaryAction.onClick,
                            enabled = primaryAction.enabled,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                        ) {
                            Text(primaryAction.label)
                        }
                        secondaryAction?.let { action ->
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = action.onClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = colors.accent
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(action.label)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val colors = LocalOnboardingColors.current
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(colors.gradientStart, colors.gradientEnd)
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
private fun SpacerHeight(height: Dp) {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(height))
}
