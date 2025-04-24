package com.example.habittracker

import androidx.annotation.WorkerThread
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitDao
import com.example.habittracker.data.HabitEntry
import com.example.habittracker.data.HabitEntryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitEntryDao: HabitEntryDao
) {
    // Habit operations
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    
    fun getHabitById(id: Int): Flow<Habit> {
        return habitDao.getHabitById(id)
    }
    
    fun searchHabits(query: String): Flow<List<Habit>> {
        return habitDao.searchHabits("%$query%")
    }
    
    @WorkerThread
    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insert(habit)
    }
    
    @WorkerThread
    suspend fun updateHabit(habit: Habit) {
        habitDao.update(habit)
    }
    
    @WorkerThread
    suspend fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)
        habitEntryDao.deleteAllForHabit(habit.id)
    }
    
    @WorkerThread
    suspend fun deleteHabitById(habitId: Int) {
        habitDao.deleteById(habitId)
        habitEntryDao.deleteAllForHabit(habitId)
    }
    
    // Habit Entry operations
    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>> {
        return habitEntryDao.getEntriesForHabit(habitId)
    }
    
    fun getEntriesForDate(date: String): Flow<List<HabitEntry>> {
        return habitEntryDao.getEntriesForDate(date)
    }
    
    fun getAllEntries(): Flow<List<HabitEntry>> {
        return habitEntryDao.getAllEntries()
    }
    
    fun getEntryForHabitAndDate(habitId: Int, date: String): Flow<HabitEntry?> {
        return habitEntryDao.getEntryForHabitAndDate(habitId, date)
    }
    
    @WorkerThread
    suspend fun insertEntry(entry: HabitEntry): Long {
        return habitEntryDao.insert(entry)
    }
    
    @WorkerThread
    suspend fun updateEntry(entry: HabitEntry) {
        habitEntryDao.update(entry)
    }
    
    @WorkerThread
    suspend fun deleteEntry(entry: HabitEntry) {
        habitEntryDao.delete(entry)
    }
    
    // Statistics operations
    fun getCompletionRate(habitId: Int): Flow<Int> {
        return habitEntryDao.getCompletedCountForHabit(habitId).map { completedCount ->
            val totalCount = habitEntryDao.getTotalCountForHabit(habitId).map { it }.first()
            if (totalCount > 0) (completedCount * 100) / totalCount else 0
        }
    }
    
    fun getTotalEntries(habitId: Int): Flow<Int> {
        return habitEntryDao.getTotalCountForHabit(habitId)
    }
    
    fun getLastCompletedEntry(habitId: Int): Flow<HabitEntry?> {
        return habitEntryDao.getLastCompletedEntry(habitId)
    }
    
    fun getCompletedCountBetweenDates(habitId: Int, startDate: String, endDate: String): Flow<Int> {
        return habitEntryDao.getCompletedCountBetweenDates(habitId, startDate, endDate)
    }
}
