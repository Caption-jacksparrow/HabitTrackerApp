package com.example.habittracker.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Utility class for habit tracking related calculations
 */
object HabitTrackingUtils {
    
    /**
     * Calculate the current streak for a habit based on its completion entries
     */
    fun calculateCurrentStreak(entries: List<com.example.habittracker.data.HabitEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val sortedEntries = entries.sortedByDescending { it.date }
        var streak = 0
        var currentDate = LocalDate.now()
        
        for (entry in sortedEntries) {
            val entryDate = LocalDate.parse(entry.date, DateTimeFormatter.ISO_LOCAL_DATE)
            val daysBetween = ChronoUnit.DAYS.between(entryDate, currentDate)
            
            // If there's a gap larger than 1 day, streak is broken
            if (daysBetween > 1) break
            
            // If this entry is completed, add to streak
            if (entry.completed) {
                streak++
                currentDate = entryDate.minusDays(1)
            } else {
                // If not completed, streak is broken
                break
            }
        }
        
        return streak
    }
    
    /**
     * Calculate the longest streak for a habit based on its completion entries
     */
    fun calculateLongestStreak(entries: List<com.example.habittracker.data.HabitEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val sortedEntries = entries.sortedBy { it.date }
        var longestStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null
        
        for (entry in sortedEntries) {
            if (entry.completed) {
                val entryDate = LocalDate.parse(entry.date, DateTimeFormatter.ISO_LOCAL_DATE)
                
                if (lastDate == null) {
                    // First completed entry
                    currentStreak = 1
                } else {
                    val daysBetween = ChronoUnit.DAYS.between(lastDate, entryDate)
                    
                    if (daysBetween == 1L) {
                        // Consecutive day
                        currentStreak++
                    } else if (daysBetween > 1L) {
                        // Gap in streak
                        currentStreak = 1
                    }
                    // If daysBetween == 0, it's the same day, don't count twice
                }
                
                lastDate = entryDate
                longestStreak = maxOf(longestStreak, currentStreak)
            }
        }
        
        return longestStreak
    }
    
    /**
     * Calculate completion rate as a percentage
     */
    fun calculateCompletionRate(entries: List<com.example.habittracker.data.HabitEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val completedEntries = entries.count { it.completed }
        return (completedEntries * 100) / entries.size
    }
    
    /**
     * Determine if a habit should be completed today based on its frequency
     */
    fun shouldCompleteToday(habit: com.example.habittracker.data.Habit): Boolean {
        val today = LocalDate.now()
        val startDate = LocalDate.parse(habit.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        
        // If start date is in the future, don't complete
        if (startDate.isAfter(today)) return false
        
        return when (habit.frequency) {
            "DAILY" -> true
            "WEEKLY" -> {
                val daysSinceStart = ChronoUnit.DAYS.between(startDate, today)
                daysSinceStart % 7 == 0L
            }
            "MONTHLY" -> {
                today.dayOfMonth == startDate.dayOfMonth
            }
            "CUSTOM" -> {
                if (habit.customFrequencyTimes <= 0 || habit.customFrequencyPeriod.isEmpty()) {
                    return false
                }
                
                val daysSinceStart = ChronoUnit.DAYS.between(startDate, today)
                
                when (habit.customFrequencyPeriod.lowercase()) {
                    "day" -> daysSinceStart % habit.customFrequencyTimes == 0L
                    "week" -> daysSinceStart % (7 * habit.customFrequencyTimes) == 0L
                    "month" -> {
                        val monthsSinceStart = ChronoUnit.MONTHS.between(startDate, today)
                        monthsSinceStart % habit.customFrequencyTimes == 0L && today.dayOfMonth == startDate.dayOfMonth
                    }
                    else -> false
                }
            }
            else -> false
        }
    }
}
