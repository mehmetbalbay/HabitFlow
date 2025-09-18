package com.habitflow.feature.onboarding

import kotlin.math.floor

object TimeUtils {
    fun parseHm(hm: String): Int {
        val parts = hm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return (h.coerceIn(0, 23) * 60 + m.coerceIn(0, 59))
    }

    fun formatHm(totalMinutes: Int): String {
        var t = ((totalMinutes % (24 * 60)) + (24 * 60)) % (24 * 60)
        val h = floor(t / 60f).toInt()
        val m = t % 60
        return "%02d:%02d".format(h, m)
    }

    fun plusMinutes(hm: String, delta: Int): String = formatHm(parseHm(hm) + delta)
}

