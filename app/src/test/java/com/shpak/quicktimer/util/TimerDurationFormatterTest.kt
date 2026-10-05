package com.shpak.quicktimer.util

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimerDurationFormatterTest {
    @Test
    fun `positive fractions round up so zero is reserved for expiry`() {
        assertEquals("0:01", 1L.toTimerDisplay(Locale.US))
        assertEquals("1:00", 59_001L.toTimerDisplay(Locale.US))
        assertEquals("0:00", 0L.toTimerDisplay(Locale.US))
        assertEquals("0:00", (-1L).toTimerDisplay(Locale.US))
    }

    @Test
    fun `hours are shown when needed without wrapping at a day`() {
        assertEquals("4:32", 272_000L.toTimerDisplay(Locale.US))
        assertEquals("1:00:00", 3_600_000L.toTimerDisplay(Locale.US))
        assertEquals("25:01:02", 90_062_000L.toTimerDisplay(Locale.US))
    }

    @Test
    fun `rounding cannot overflow`() {
        assertEquals(Long.MAX_VALUE / 1_000L + 1L, Long.MAX_VALUE.toTimerSeconds())
    }

    @Test
    fun `display uses locale digits`() {
        assertEquals("٤:٣٢", 272_000L.toTimerDisplay(Locale.forLanguageTag("ar")))
    }
}