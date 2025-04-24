package com.example.habittracker.ui.statistics

import androidx.lifecycle.*
import com.example.habittracker.HabitRepository
import com.example.habittracker.data.Habit
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class StatisticsViewModel(private val repository: HabitRepository) : ViewModel() {

    // Get all habits from the repository
    val allHabits: LiveData<List<Habit>> = repository.allHabits.asLiveData()
    
    // Get overall completion rate for all habits
    val overallCompletionRate: LiveData<Int> = repository.allHabits
        .combine(repository.getAllEntries()) { habits, entries ->
            if (entries.isEmpty()) return@combine 0
            
            val completedEntries = entries.count { it.completed }
            (completedEntries * 100 / entries.size)
        }
        .asLiveData()
    
    // Get statistics for a specific habit
    fun getHabitStatistics(habitId: Int): LiveData<HabitStats> {
        val habit = repository.getHabitById(habitId).asLiveData()
        val entries = repository.getEntriesForHabit(habitId).asLiveData()
        
        return MediatorLiveData<HabitStats>().apply {
            addSource(habit) { updateStats(this, habit.value, entries.value) }
            addSource(entries) { updateStats(this, habit.value, entries.value) }
        }
    }
    
    private fun updateStats(
        result: MutableLiveData<HabitStats>,
        habit: Habit?,
        entries: List<com.example.habittracker.data.HabitEntry>?
    ) {
        if (habit == null || entries == null) return
        
        val completedEntries = entries.filter { it.completed }
        val completionRate = if (entries.isNotEmpty()) {
            (completedEntries.size * 100) / entries.size
        } else {
            0
        }
        
        // Calculate current streak
        val currentStreak = calculateCurrentStreak(entries)
        
        // Calculate longest streak
        val longestStreak = calculateLongestStreak(entries)
        
        result.value = HabitStats(
            completionRate = completionRate,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalCompletions = completedEntries.size,
            totalDays = entries.size
        )
    }
    
    private fun calculateCurrentStreak(entries: List<com.example.habittracker.data.HabitEntry>): Int {
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
    
    private fun calculateLongestStreak(entries: List<com.example.habittracker.data.HabitEntry>): Int {
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
}

data class HabitStats(
    val completionRate: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
    val totalDays: Int
)

class StatisticsViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
