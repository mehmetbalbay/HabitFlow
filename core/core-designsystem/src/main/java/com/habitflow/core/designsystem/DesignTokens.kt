package com.habitflow.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DesignTokens {
    val screenPadding: Dp = 24.dp
    val sectionSpacing: Dp = 20.dp
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFF9FAFB), Color(0xFFF0F8FF))
    )
    val cardShape = RoundedCornerShape(24.dp)
    val largeCardShape = RoundedCornerShape(28.dp)
    val capsuleShape = RoundedCornerShape(50)

    val heroGradient = Brush.linearGradient(
        listOf(Color(0xFF6366F1), Color(0xFF4338CA))
    )
    val metricBlueGradient = Brush.linearGradient(
        listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
    )
    val metricPinkGradient = Brush.linearGradient(
        listOf(Color(0xFFFDA4AF), Color(0xFFF43F5E))
    )
    val chartGradient = Brush.linearGradient(
        listOf(Color(0xFF34D399), Color(0xFF0EA5E9))
    )

    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF4B5563)
    val badgeBackground = Color(0x1A4F46E5)
    val badgeText = Color(0xFF4338CA)
    val progressTrack = Color(0xFFE2E8F0)
    val progressFill = Color(0xFF4F46E5)
}

