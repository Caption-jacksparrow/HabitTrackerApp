package com.example.habittracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY name ASC")
    fun getAllHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Int): Flow<Habit>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long
    
    @Update
    suspend fun update(habit: Habit)
    
    @Delete
    suspend fun delete(habit: Habit)
    
    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteById(habitId: Int)
    
    @Query("SELECT * FROM habits WHERE name LIKE :searchQuery")
    fun searchHabits(searchQuery: String): Flow<List<Habit>>
}
