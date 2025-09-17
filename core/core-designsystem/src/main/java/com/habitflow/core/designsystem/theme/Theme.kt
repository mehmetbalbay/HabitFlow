package com.habitflow.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Secondary.copy(alpha = 0.16f),
    onSecondaryContainer = OnBackgroundLight,
    tertiary = Tertiary,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = OnSurfaceLight.copy(alpha = 0.7f),
    outline = OutlineLight
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimaryDark,
    secondary = Secondary,
    onSecondary = Color.Black,
    secondaryContainer = Secondary.copy(alpha = 0.3f),
    onSecondaryContainer = OnBackgroundDark,
    tertiary = Tertiary,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark.copy(alpha = 0.7f),
    outline = OutlineDark
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HabitFlowTypography,
        content = content
    )
}

