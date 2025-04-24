package com.example.habittracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitEntryDao {
    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId ORDER BY date DESC")
    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>>
    
    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<HabitEntry>>
    
    @Query("SELECT * FROM habit_entries")
    fun getAllEntries(): Flow<List<HabitEntry>>
    
    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date")
    fun getEntryForHabitAndDate(habitId: Int, date: String): Flow<HabitEntry?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HabitEntry): Long
    
    @Update
    suspend fun update(entry: HabitEntry)
    
    @Delete
    suspend fun delete(entry: HabitEntry)
    
    @Query("DELETE FROM habit_entries WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Int)
    
    @Query("SELECT COUNT(*) FROM habit_entries WHERE habitId = :habitId AND completed = 1")
    fun getCompletedCountForHabit(habitId: Int): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM habit_entries WHERE habitId = :habitId")
    fun getTotalCountForHabit(habitId: Int): Flow<Int>
    
    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND completed = 1 ORDER BY date DESC LIMIT 1")
    fun getLastCompletedEntry(habitId: Int): Flow<HabitEntry?>
    
    @Query("SELECT COUNT(*) FROM habit_entries WHERE habitId = :habitId AND completed = 1 AND date BETWEEN :startDate AND :endDate")
    fun getCompletedCountBetweenDates(habitId: Int, startDate: String, endDate: String): Flow<Int>
}
