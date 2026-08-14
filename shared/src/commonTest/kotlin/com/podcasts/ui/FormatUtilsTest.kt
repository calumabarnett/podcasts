package com.podcasts.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {

    @Test
    fun testFormatDurationSeconds() {
        assertEquals("45m 0s", formatDuration(2700))
        assertEquals("1h 15m", formatDuration(4500))
        assertEquals("30s", formatDuration(30))
    }

    @Test
    fun testFormatTimeMilliseconds() {
        assertEquals("0:05", formatTime(5000))
        assertEquals("1:15", formatTime(75000))
        assertEquals("10:00", formatTime(600000))
    }

    @Test
    fun testFormatDateMilliseconds() {
        // 1655298000000 ms is Wed, 15 Jun 2022 13:00:00 GMT
        assertEquals("Jun 15, 2022", formatDate(1655298000000L))

        // 0 / negative fallback
        assertEquals("Unknown date", formatDate(0L))
    }
}
