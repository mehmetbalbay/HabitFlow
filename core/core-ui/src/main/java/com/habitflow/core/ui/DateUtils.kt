package com.habitflow.core.ui

import com.habitflow.domain.model.DayCount
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.WeekProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object DateUtils {
    private val keyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE", Locale("tr"))
    private val dayMonthFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("tr"))

    fun todayKey(): String = dateKey(LocalDate.now())

    fun dateKey(date: LocalDate): String = keyFormatter.format(date)

    fun parseDate(key: String): LocalDate = LocalDate.parse(key, keyFormatter)

    fun lastDays(count: Int): List<LocalDate> {
        val today = LocalDate.now()
        return (count - 1 downTo 0).map { today.minusDays(it.toLong()) }
    }

    fun dailyCounts(habits: List<Habit>): List<DayCount> {
        val days = lastDays(7)
        val values = days.map { date ->
            val key = dateKey(date)
            val total = habits.count { habit -> habit.history[key] == true }
            DayCount(label = weekdayFormatter.format(date), count = total)
        }
        return values
    }

    fun weeklyPercentages(habits: List<Habit>): List<WeekProgress> {
        if (habits.isEmpty()) return List(4) { index ->
            val end = LocalDate.now().minusWeeks((3 - index).toLong())
            WeekProgress(label = weekLabel(end), percentage = 0)
        }
        val weeks = mutableListOf<WeekWindow>()
        var cursor = LocalDate.now()
        repeat(4) {
            val end = cursor
            val start = cursor.minusDays(6)
            weeks.add(WeekWindow(start, end))
            cursor = cursor.minusWeeks(1)
        }
        val totalPossiblePerWeek = habits.size * 7
        val result = weeks.map { window ->
            val completed = habits.fold(0) { acc, habit ->
                acc + habit.history.entries.count { (dateKey, isDone) ->
                    if (!isDone) return@count false
                    val date = runCatching { parseDate(dateKey) }.getOrNull() ?: return@count false
                    !date.isBefore(window.start) && !date.isAfter(window.end)
                }
            }
            val percentage = if (totalPossiblePerWeek == 0) 0 else ((completed.toDouble() / totalPossiblePerWeek) * 100).roundToInt()
            WeekProgress(label = weekLabel(window.end), percentage = percentage.coerceIn(0, 100))
        }
        return result.reversed()
    }

    private fun weekLabel(end: LocalDate): String {
        val start = end.minusDays(6)
        return "${dayMonthFormatter.format(start)} - ${dayMonthFormatter.format(end)}"
    }

    private data class WeekWindow(val start: LocalDate, val end: LocalDate)
}

