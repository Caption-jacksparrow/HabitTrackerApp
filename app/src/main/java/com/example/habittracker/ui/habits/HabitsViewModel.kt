package com.example.habittracker.ui.habits

import androidx.lifecycle.*
import com.example.habittracker.HabitRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {

    // Get all habits from the repository
    val allHabits: LiveData<List<Habit>> = repository.allHabits.asLiveData()
    
    // Function to get a habit by ID
    fun getHabitById(habitId: Int): LiveData<Habit> {
        return repository.getHabitById(habitId).asLiveData()
    }
    
    // Function to insert a new habit
    fun insert(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }
    
    // Function to update an existing habit
    fun update(habit: Habit) = viewModelScope.launch {
        repository.updateHabit(habit)
    }
    
    // Function to delete a habit
    fun delete(habit: Habit) = viewModelScope.launch {
        repository.deleteHabit(habit)
    }
    
    // Function to delete a habit by ID
    fun deleteHabitById(habitId: Int) = viewModelScope.launch {
        repository.deleteHabitById(habitId)
    }
    
    // Function to mark a habit as completed for today
    fun toggleHabitCompletion(habit: Habit, isCompleted: Boolean) = viewModelScope.launch {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        // Check if an entry already exists for today
        val existingEntry = repository.getEntryForHabitAndDate(habit.id, today).first()
        
        if (existingEntry != null) {
            // Update existing entry
            repository.updateEntry(existingEntry.copy(completed = isCompleted))
        } else {
            // Create new entry
            val newEntry = HabitEntry(
                habitId = habit.id,
                date = today,
                completed = isCompleted
            )
            repository.insertEntry(newEntry)
        }
    }
    
    // Function to get completion status for a habit today
    fun getHabitCompletionForToday(habitId: Int): LiveData<Boolean> {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return repository.getEntryForHabitAndDate(habitId, today).asLiveData().map { entry ->
            entry?.completed ?: false
        }
    }
    
    // Function to get habit statistics
    fun getHabitStatistics(habitId: Int): LiveData<HabitStats> {
        return MediatorLiveData<HabitStats>().apply {
            val entriesLiveData = repository.getEntriesForHabit(habitId).asLiveData()
            
            addSource(entriesLiveData) { entries ->
                val stats = calculateHabitStats(entries)
                value = stats
            }
        }
    }
    
    private fun calculateHabitStats(entries: List<HabitEntry>): HabitStats {
        if (entries.isEmpty()) {
            return HabitStats(0, 0, 0)
        }
        
        val completedEntries = entries.filter { it.completed }
        val completionRate = (completedEntries.size * 100) / entries.size
        
        // Calculate current streak
        val sortedEntries = entries.sortedByDescending { it.date }
        var currentStreak = 0
        var currentDate = LocalDate.now()
        
        for (entry in sortedEntries) {
            val entryDate = LocalDate.parse(entry.date, DateTimeFormatter.ISO_LOCAL_DATE)
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(entryDate, currentDate)
            
            if (daysBetween > 1) break // Streak broken by gap
            
            if (entry.completed) {
                currentStreak++
                currentDate = entryDate.minusDays(1)
            } else {
                break // Streak broken by non-completion
            }
        }
        
        // Calculate longest streak
        var longestStreak = 0
        var tempStreak = 0
        var lastDate: LocalDate? = null
        
        for (entry in entries.filter { it.completed }.sortedBy { it.date }) {
            val entryDate = LocalDate.parse(entry.date, DateTimeFormatter.ISO_LOCAL_DATE)
            
            if (lastDate == null) {
                tempStreak = 1
            } else {
                val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, entryDate)
                
                if (daysBetween == 1L) {
                    tempStreak++
                } else {
                    tempStreak = 1
                }
            }
            
            lastDate = entryDate
            longestStreak = maxOf(longestStreak, tempStreak)
        }
        
        return HabitStats(completionRate, currentStreak, longestStreak)
    }
}

data class HabitStats(
    val completionRate: Int,
    val currentStreak: Int,
    val longestStreak: Int
)
