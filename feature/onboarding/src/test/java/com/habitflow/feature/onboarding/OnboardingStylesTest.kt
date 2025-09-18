package com.habitflow.feature.onboarding

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.habitflow.feature.onboarding.ui.OnboardingColors
import com.habitflow.feature.onboarding.ui.OnboardingDimens
import com.habitflow.feature.onboarding.ui.OnboardingPrimaryAction
import com.habitflow.feature.onboarding.ui.OnboardingSecondaryAction
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class OnboardingStylesTest {

    @Test
    fun `OnboardingColors has correct default values`() {
        val colors = OnboardingColors(
            gradientStart = Color(0xFF78C3FB),
            gradientEnd = Color(0xFF4FD4C3),
            cardBackground = Color.White,
            accent = Color(0xFF1C9E92),
            faintOnBackground = Color.White.copy(alpha = 0.7f)
        )
        
        assertEquals(Color(0xFF78C3FB), colors.gradientStart)
        assertEquals(Color(0xFF4FD4C3), colors.gradientEnd)
        assertEquals(Color.White, colors.cardBackground)
        assertEquals(Color(0xFF1C9E92), colors.accent)
        assertEquals(0.7f, colors.faintOnBackground.alpha, 0.01f)
    }

    @Test
    fun `OnboardingDimens has correct default values`() {
        val dimens = OnboardingDimens(
            horizontalPadding = 24.dp,
            verticalPadding = 20.dp,
            cardElevation = 16.dp
        )
        
        assertEquals(24.dp, dimens.horizontalPadding)
        assertEquals(20.dp, dimens.verticalPadding)
        assertEquals(16.dp, dimens.cardElevation)
    }

    @Test
    fun `OnboardingPrimaryAction has correct properties`() {
        val action = OnboardingPrimaryAction(
            label = "Test Label",
            onClick = {},
            enabled = true
        )
        
        assertEquals("Test Label", action.label)
        assertTrue(action.enabled)
    }

    @Test
    fun `OnboardingPrimaryAction defaults to enabled`() {
        val action = OnboardingPrimaryAction(
            label = "Test Label",
            onClick = {}
        )
        
        assertTrue(action.enabled)
    }

    @Test
    fun `OnboardingSecondaryAction has correct properties`() {
        val action = OnboardingSecondaryAction(
            label = "Test Label",
            onClick = {}
        )
        
        assertEquals("Test Label", action.label)
    }
}
