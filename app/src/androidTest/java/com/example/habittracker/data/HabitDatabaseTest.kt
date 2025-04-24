package com.example.habittracker.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class HabitDatabaseTest {
    private lateinit var habitDao: HabitDao
    private lateinit var habitEntryDao: HabitEntryDao
    private lateinit var db: HabitDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, HabitDatabase::class.java
        ).build()
        habitDao = db.habitDao()
        habitEntryDao = db.habitEntryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetHabit() = runBlocking {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val habit = Habit(
            name = "Test Habit",
            description = "Test Description",
            frequency = "DAILY",
            color = "#FF0000",
            startDate = today,
            createdAt = today,
            updatedAt = today
        )
        
        val habitId = habitDao.insert(habit)
        val retrievedHabit = habitDao.getHabitById(habitId.toInt()).first()
        
        assertEquals(habit.name, retrievedHabit.name)
        assertEquals(habit.description, retrievedHabit.description)
        assertEquals(habit.frequency, retrievedHabit.frequency)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetHabitEntry() = runBlocking {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        // First create a habit
        val habit = Habit(
            name = "Test Habit",
            description = "Test Description",
            frequency = "DAILY",
            color = "#FF0000",
            startDate = today,
            createdAt = today,
            updatedAt = today
        )
        val habitId = habitDao.insert(habit).toInt()
        
        // Now create an entry for this habit
        val entry = HabitEntry(
            habitId = habitId,
            date = today,
            completed = true
        )
        val entryId = habitEntryDao.insert(entry)
        
        // Retrieve the entry
        val retrievedEntry = habitEntryDao.getEntryForHabitAndDate(habitId, today).first()
        
        assertNotNull(retrievedEntry)
        assertEquals(true, retrievedEntry?.completed)
        assertEquals(habitId, retrievedEntry?.habitId)
    }

    @Test
    @Throws(Exception::class)
    fun updateHabit() = runBlocking {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val habit = Habit(
            name = "Original Name",
            description = "Original Description",
            frequency = "DAILY",
            color = "#FF0000",
            startDate = today,
            createdAt = today,
            updatedAt = today
        )
        
        val habitId = habitDao.insert(habit).toInt()
        
        // Update the habit
        val updatedHabit = habit.copy(
            id = habitId,
            name = "Updated Name",
            description = "Updated Description"
        )
        habitDao.update(updatedHabit)
        
        // Retrieve the updated habit
        val retrievedHabit = habitDao.getHabitById(habitId).first()
        
        assertEquals("Updated Name", retrievedHabit.name)
        assertEquals("Updated Description", retrievedHabit.description)
    }

    @Test
    @Throws(Exception::class)
    fun deleteHabit() = runBlocking {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val habit = Habit(
            name = "Test Habit",
            description = "Test Description",
            frequency = "DAILY",
            color = "#FF0000",
            startDate = today,
            createdAt = today,
            updatedAt = today
        )
        
        val habitId = habitDao.insert(habit).toInt()
        
        // Verify habit exists
        var allHabits = habitDao.getAllHabits().first()
        assertEquals(1, allHabits.size)
        
        // Delete the habit
        habitDao.deleteById(habitId)
        
        // Verify habit is deleted
        allHabits = habitDao.getAllHabits().first()
        assertEquals(0, allHabits.size)
    }
}
