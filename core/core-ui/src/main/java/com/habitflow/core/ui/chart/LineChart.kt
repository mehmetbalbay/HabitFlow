package com.habitflow.core.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6D5E55)
) {
    if (points.isEmpty()) return
    Canvas(modifier = modifier.fillMaxWidth().height(80.dp)) {
        val stepX = if (points.size > 1) size.width / (points.size - 1) else size.width
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = (max - min).takeIf { it > 0f } ?: 1f
        fun norm(v: Float) = (v - min) / range

        val path = Path()
        points.forEachIndexed { i, v ->
            val x = i * stepX
            val y = size.height - norm(v) * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color)
    }
}

