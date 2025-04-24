package com.example.habittracker.utils

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.habittracker.data.HabitEntry

class HabitTrackingUtilsTest {

    @Test
    fun testCalculateCurrentStreak_emptyEntries() {
        val entries = emptyList<HabitEntry>()
        val streak = HabitTrackingUtils.calculateCurrentStreak(entries)
        assertEquals(0, streak)
    }

    @Test
    fun testCalculateCurrentStreak_consecutiveDays() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        val entries = listOf(
            HabitEntry(habitId = 1, date = today.format(formatter), completed = true),
            HabitEntry(habitId = 1, date = today.minusDays(1).format(formatter), completed = true),
            HabitEntry(habitId = 1, date = today.minusDays(2).format(formatter), completed = true)
        )
        
        val streak = HabitTrackingUtils.calculateCurrentStreak(entries)
        assertEquals(3, streak)
    }

    @Test
    fun testCalculateCurrentStreak_brokenStreak() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        val entries = listOf(
            HabitEntry(habitId = 1, date = today.format(formatter), completed = true),
            HabitEntry(habitId = 1, date = today.minusDays(1).format(formatter), completed = false),
            HabitEntry(habitId = 1, date = today.minusDays(2).format(formatter), completed = true)
        )
        
        val streak = HabitTrackingUtils.calculateCurrentStreak(entries)
        assertEquals(1, streak)
    }

    @Test
    fun testCalculateCurrentStreak_gapInDates() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        val entries = listOf(
            HabitEntry(habitId = 1, date = today.format(formatter), completed = true),
            HabitEntry(habitId = 1, date = today.minusDays(3).format(formatter), completed = true)
        )
        
        val streak = HabitTrackingUtils.calculateCurrentStreak(entries)
        assertEquals(1, streak)
    }

    @Test
    fun testCalculateCompletionRate() {
        val entries = listOf(
            HabitEntry(habitId = 1, date = "2025-04-24", completed = true),
            HabitEntry(habitId = 1, date = "2025-04-23", completed = true),
            HabitEntry(habitId = 1, date = "2025-04-22", completed = false),
            HabitEntry(habitId = 1, date = "2025-04-21", completed = true)
        )
        
        val rate = HabitTrackingUtils.calculateCompletionRate(entries)
        assertEquals(75, rate)
    }

    @Test
    fun testCalculateCompletionRate_emptyEntries() {
        val entries = emptyList<HabitEntry>()
        val rate = HabitTrackingUtils.calculateCompletionRate(entries)
        assertEquals(0, rate)
    }

    @Test
    fun testCalculateCompletionRate_allCompleted() {
        val entries = listOf(
            HabitEntry(habitId = 1, date = "2025-04-24", completed = true),
            HabitEntry(habitId = 1, date = "2025-04-23", completed = true)
        )
        
        val rate = HabitTrackingUtils.calculateCompletionRate(entries)
        assertEquals(100, rate)
    }

    @Test
    fun testCalculateCompletionRate_noneCompleted() {
        val entries = listOf(
            HabitEntry(habitId = 1, date = "2025-04-24", completed = false),
            HabitEntry(habitId = 1, date = "2025-04-23", completed = false)
        )
        
        val rate = HabitTrackingUtils.calculateCompletionRate(entries)
        assertEquals(0, rate)
    }
}
