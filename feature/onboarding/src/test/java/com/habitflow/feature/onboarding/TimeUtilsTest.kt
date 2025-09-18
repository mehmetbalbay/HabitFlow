package com.habitflow.feature.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun `parseHm returns correct minutes for valid time`() {
        assertEquals(0, TimeUtils.parseHm("00:00"))
        assertEquals(60, TimeUtils.parseHm("01:00"))
        assertEquals(90, TimeUtils.parseHm("01:30"))
        assertEquals(1380, TimeUtils.parseHm("24:00")) // 24:00 becomes 23:00 due to coerce (23*60)
        assertEquals(1439, TimeUtils.parseHm("23:59"))
    }

    @Test
    fun `parseHm coerces invalid time to valid range`() {
        assertEquals(1380, TimeUtils.parseHm("25:00")) // 25:00 becomes 23:00 due to coerce (23*60)
        assertEquals(779, TimeUtils.parseHm("12:60")) // 12:60 becomes 12:59 due to coerce (12*60 + 59)
        assertEquals(0, TimeUtils.parseHm("invalid")) // invalid becomes 00:00 due to coerce
        assertEquals(0, TimeUtils.parseHm("")) // empty becomes 00:00 due to coerce
        assertEquals(720, TimeUtils.parseHm("12")) // 12 becomes 12:00 due to coerce (12*60)
    }

    @Test
    fun `plusMinutes adds minutes correctly`() {
        assertEquals("01:00", TimeUtils.plusMinutes("00:30", 30))
        assertEquals("02:00", TimeUtils.plusMinutes("01:30", 30))
        assertEquals("00:00", TimeUtils.plusMinutes("23:30", 30))
        assertEquals("01:00", TimeUtils.plusMinutes("00:30", 30))
    }

    @Test
    fun `plusMinutes handles day overflow`() {
        assertEquals("00:00", TimeUtils.plusMinutes("23:30", 30))
        assertEquals("01:00", TimeUtils.plusMinutes("23:30", 90))
    }

    @Test
    fun `formatHm returns correct time string`() {
        assertEquals("00:00", TimeUtils.formatHm(0))
        assertEquals("01:00", TimeUtils.formatHm(60))
        assertEquals("01:30", TimeUtils.formatHm(90))
        assertEquals("23:59", TimeUtils.formatHm(1439))
    }

    @Test
    fun `formatHm handles day overflow`() {
        assertEquals("00:00", TimeUtils.formatHm(1440))
        assertEquals("01:00", TimeUtils.formatHm(1500))
    }
}
