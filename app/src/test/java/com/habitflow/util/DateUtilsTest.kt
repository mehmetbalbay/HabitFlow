package com.habitflow.util

import com.habitflow.domain.model.Habit
import com.habitflow.core.ui.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun `dateKey round trip returns original date`() {
        val date = LocalDate.of(2024, 1, 15)

        val key = DateUtils.dateKey(date)
        val parsed = DateUtils.parseDate(key)

        assertEquals(date, parsed)
    }

    @Test
    fun `lastDays returns consecutive days ending today`() {
        val count = 5
        val result = DateUtils.lastDays(count)
        val today = LocalDate.now()

        assertEquals(count, result.size)
        assertEquals(today, result.last())
        assertEquals(today.minusDays((count - 1).toLong()), result.first())
        assertTrue(result.zipWithNext().all { (current, next) -> current.plusDays(1) == next })
    }

    @Test
    fun `dailyCounts maps habit completions per day`() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)
        val habitA = Habit(
            id = "1",
            name = "Run",
            createdAt = DateUtils.dateKey(twoDaysAgo),
            history = mapOf(
                DateUtils.dateKey(today) to true,
                DateUtils.dateKey(yesterday) to true
            )
        )
        val habitB = Habit(
            id = "2",
            name = "Read",
            createdAt = DateUtils.dateKey(twoDaysAgo),
            history = mapOf(
                DateUtils.dateKey(today) to true,
                DateUtils.dateKey(twoDaysAgo) to true
            )
        )

        val counts = DateUtils.dailyCounts(listOf(habitA, habitB))

        val todayCount = counts.last().count
        val yesterdayCount = counts[counts.size - 2].count
        assertEquals(2, todayCount)
        assertEquals(1, yesterdayCount)
    }

    @Test
    fun `weeklyPercentages returns four weeks with recent progress`() {
        val today = LocalDate.now()
        val currentWeekHistory = (0..6).associate { offset ->
            val date = today.minusDays(offset.toLong())
            DateUtils.dateKey(date) to true
        }
        val habit = Habit(
            id = "1",
            name = "Meditate",
            createdAt = DateUtils.dateKey(today.minusWeeks(4)),
            history = currentWeekHistory
        )

        val weekProgress = DateUtils.weeklyPercentages(listOf(habit))

        assertEquals(4, weekProgress.size)
        assertEquals(0, weekProgress[0].percentage)
        assertEquals(0, weekProgress[1].percentage)
        assertEquals(0, weekProgress[2].percentage)
        assertEquals(100, weekProgress[3].percentage)
    }
}
